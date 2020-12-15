package com.jefff.exercise.io.input;

import java.io.*;

public class LineReader {
    String fileName;
    BufferedReader bufferedReader;
    File fileSource;

    public LineReader(String fileName) {
        this.fileName = fileName;
        fileSource = null;
    }

    public LineReader(File fileSource) {
        this.fileSource = fileSource;
        fileName = null;
    }

    public void open() throws FileNotFoundException {
        FileReader fileReader;
        if (fileName != null) {
            fileReader = new FileReader(fileName);
        } else {
            fileReader = new FileReader(fileSource);
        }
        bufferedReader = new BufferedReader(fileReader);
    }

    public String readLine() throws IOException {
        String line = bufferedReader.readLine();
        return line;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
}
