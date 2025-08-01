package com.example.pdfeditor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import static org.apache.pdfbox.io.MemoryUsageSetting.setupMainMemoryOnly;

@Component
public class PdfEditor {

    private final PdfCredentials pdfCredentials;

    public PdfEditor(PdfCredentials pdfCredentials) {
        this.pdfCredentials = pdfCredentials;
    }

    void mergeUsingPDFBox(List<String> pdfFiles, String outputFile, String path) throws IOException {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(outputFile);

        pdfFiles.forEach(file -> {
            try {
                String unlockedFileName = file.replace(".pdf", "_unlocked.pdf");
                PDDocument doc1 = Loader.loadPDF(new File(path + "/" + file), pdfCredentials.getPassword());
                doc1.setAllSecurityToBeRemoved(true);
                doc1.save(unlockedFileName);
                pdfMergerUtility.addSource(new File(unlockedFileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        pdfMergerUtility.mergeDocuments(setupMainMemoryOnly().streamCache);
    }

    void unlockPDF(List<String> pdfFiles, String path) {

        pdfFiles.forEach(file -> {
            try {
                String unlockedFileName = file.replace(".pdf", "_unlocked.pdf");
                PDDocument unlockedDocument = Loader.loadPDF(new File(path + "/" + file), pdfCredentials.getPassword());
                unlockedDocument.setAllSecurityToBeRemoved(true);
                // fix this line to effectively save files on the working directory
                unlockedDocument.save(unlockedFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
