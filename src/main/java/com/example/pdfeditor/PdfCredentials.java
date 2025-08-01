package com.example.pdfeditor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PdfCredentials {
    @Value("${pdf.password}")
    private String password;

    public String getPassword() {
        return password;
    }
}
