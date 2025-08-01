package com.example.pdfeditor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesNameGetter {

    public static List<String> get() {
        String directoryPath = "/Users/eprie/Documents/GitHub/pdf-editor/recibos";

        File directory = new File(directoryPath);

        return Arrays.stream(directory.listFiles()).map(
            File::getName
        ).toList();
    }
}
