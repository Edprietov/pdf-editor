package com.example.pdfeditor;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class PdfEditorApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PdfEditorApplication.class, args);

        String path = context.getEnvironment().getProperty("pdf.directory");
        List<String> pdfFiles = FilesNameGetter.get(path);

        PdfEditor pdfEditor = context.getBean(PdfEditor.class);

        try {
            pdfEditor.mergeUsingPDFBox(pdfFiles, "merged.pdf", path);
        } catch (IOException e) {
            System.err.println("PDF processing failed: " + e.getMessage());
        }
    }
}
