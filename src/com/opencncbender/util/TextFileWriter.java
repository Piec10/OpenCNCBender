package com.opencncbender.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TextFileWriter {

    public static String changeExtension(String fileName, String newExtension) {

        String [] fileNameSplitted = fileName.split("\\.");
        StringBuilder newFileName = new StringBuilder();

        for(int i=0; i < fileNameSplitted.length - 1; i++){
            newFileName.append(fileNameSplitted[i]);
            newFileName.append(".");
        }
        newFileName.append(newExtension);

        return newFileName.toString();
    }

    public static void saveTextFile(File file, String content) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.print(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
