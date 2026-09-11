package com.example.pdfeditor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesNameGetter {

    public static List<String> get(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles(file -> file.isFile()
            && file.getName().toLowerCase().endsWith(".pdf")
            && !file.getName().toLowerCase().endsWith("_unlocked.pdf")
            && !file.getName().equalsIgnoreCase("merged.pdf"));

        if (files == null) {
            return List.of();
        }

        return Arrays.stream(files).map(File::getName).toList();
    }
}
