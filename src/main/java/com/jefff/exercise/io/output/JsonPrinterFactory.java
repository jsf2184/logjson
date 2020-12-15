package com.jefff.exercise.io.output;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Slf4j
public class JsonPrinterFactory {
    String filename;

    public JsonPrinterFactory(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public JsonPrinter createJsonPrinter() throws FileNotFoundException {
        return new JsonPrinter(createOutputStream());
    }

    BufferedOutputStream createOutputStream() throws FileNotFoundException {
        log.info("Creating temporary output file: {}", filename);
        BufferedOutputStream result = new BufferedOutputStream(new FileOutputStream(filename));
        return result;
    }

}
