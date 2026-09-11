package com.example.pdfeditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.apache.pdfbox.io.MemoryUsageSetting.setupMainMemoryOnly;

@Component
public class PdfEditor {

    private static final Logger logger = LoggerFactory.getLogger(PdfEditor.class);

    private final PdfCredentials pdfCredentials;

    public PdfEditor(PdfCredentials pdfCredentials) {
        this.pdfCredentials = pdfCredentials;
    }

    void mergeUsingPDFBox(List<String> pdfFiles, String outputFile, String path) throws IOException {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        File output = new File(path, outputFile);
        pdfMergerUtility.setDestinationFileName(output.getPath());
        if (output.exists() && !output.delete()) {
            logger.warn("Could not delete existing output file '{}' before merge", output.getAbsolutePath());
        }

        List<File> unlockedFiles = new ArrayList<>();
        for (String file : pdfFiles) {
            File sourceFile = new File(path, file);
            File unlockedFile = new File(path, file.replace(".pdf", "_unlocked.pdf"));
            try {
                try (PDDocument doc = Loader.loadPDF(sourceFile, pdfCredentials.getPassword())) {
                    if (unlockedFile.exists() && !unlockedFile.delete()) {
                        logger.warn("Could not delete existing file '{}' before overwrite", unlockedFile.getAbsolutePath());
                    }
                    doc.setAllSecurityToBeRemoved(true);
                    doc.save(unlockedFile);
                }
                pdfMergerUtility.addSource(unlockedFile);
                unlockedFiles.add(unlockedFile);
            } catch (IOException e) {
                logger.warn("Skipping file '{}' during merge: {}", sourceFile.getAbsolutePath(), e.getMessage());
            }
        }

        if (unlockedFiles.isEmpty()) {
            throw new IOException("No valid PDF files found to merge.");
        }

        try {
            pdfMergerUtility.mergeDocuments(setupMainMemoryOnly().streamCache);
        } finally {
            unlockedFiles.forEach(File::delete);
        }
    }
}
