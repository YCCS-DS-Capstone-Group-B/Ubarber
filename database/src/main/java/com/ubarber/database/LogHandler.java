package com.ubarber.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;



public class LogHandler {

    public static void main(String[] args) throws IOException {


        System.out.println("---------------h2 logs------------------");
        List<String> lines = readLogFile("h2Logs.txt");
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static List<String> readLogFile(String path) throws IOException {
        File logFile = new File(path);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    public static List<String> parseTraceFile(File traceFile) throws IOException {
        List<String> statements = new ArrayList<>();
        byte[] encoded = Files.readAllBytes(traceFile.toPath());
        String content = new String(encoded, StandardCharsets.UTF_8);
        String[] lines = content.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} jdbc\\[\\d+\\]: .*")) {
                if (i + 1 < lines.length && lines[i + 1].startsWith("/*SQL")) {
                    statements.add(line);
                    statements.add(lines[i + 1]);
                }
            }
        }
        return statements;
    }
}
