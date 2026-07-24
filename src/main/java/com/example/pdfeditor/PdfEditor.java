package com.example.pdfeditor;

import java.io.File;
import java.io.IOException;
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
        pdfMergerUtility.setDestinationFileName(outputFile);
        File output = new File(outputFile);
        if (output.exists() && !output.delete()) {
            logger.warn("Could not delete existing output file '{}' before merge", output.getAbsolutePath());
        }
        int validSources = 0;

        for (String file : pdfFiles) {
            File sourceFile = new File(path, file);
            File unlockedFile = new File(path, file.replace(".pdf", "_unlocked.pdf"));
            try {
                try (PDDocument doc1 = Loader.loadPDF(sourceFile, pdfCredentials.getPassword())) {
                    if (unlockedFile.exists() && !unlockedFile.delete()) {
                        logger.warn("Could not delete existing file '{}' before overwrite", unlockedFile.getAbsolutePath());
                    }
                    doc1.setAllSecurityToBeRemoved(true);
                    doc1.save(unlockedFile);
                }
                pdfMergerUtility.addSource(unlockedFile);
                validSources++;
            } catch (IOException e) {
                logger.warn("Skipping file '{}' during merge: {}", sourceFile.getAbsolutePath(), e.getMessage());
            }
        }

        if (validSources == 0) {
            throw new IOException("No valid PDF files found to merge.");
        }

        pdfMergerUtility.mergeDocuments(setupMainMemoryOnly().streamCache);
    }

    void unlockPDF(List<String> pdfFiles, String path) {

        for (String file : pdfFiles) {
            File sourceFile = new File(path, file);
            File unlockedFile = new File(path, file.replace(".pdf", "_unlocked.pdf"));
            try {
                try (PDDocument unlockedDocument = Loader.loadPDF(sourceFile, pdfCredentials.getPassword())) {
                    if (unlockedFile.exists() && !unlockedFile.delete()) {
                        logger.warn("Could not delete existing file '{}' before overwrite", unlockedFile.getAbsolutePath());
                    }
                    unlockedDocument.setAllSecurityToBeRemoved(true);
                    unlockedDocument.save(unlockedFile);
                }
            } catch (IOException e) {
                logger.warn("Skipping file '{}' during unlock: {}", sourceFile.getAbsolutePath(), e.getMessage());
            }
        }
    }
}
