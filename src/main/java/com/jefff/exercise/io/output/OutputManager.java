package com.jefff.exercise.io.output;


import com.jefff.exercise.LogEntry;
import com.jefff.exercise.LogEntryWindow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class OutputManager {

    private final String outputFileName;
    private final LogEntryWindow logEntryWindow;
    private final JsonPrinterFactory jsonPrinterFactory;
    JsonPrinter jsonPrinter;

    public OutputManager(String outputFileName,
                         LogEntryWindow logEntryWindow,
                         JsonPrinterFactory jsonPrinterFactory) {
        this.outputFileName = outputFileName;
        this.logEntryWindow = logEntryWindow;
        this.jsonPrinterFactory = jsonPrinterFactory;
        jsonPrinter = null;
    }

    public void open() throws IOException {
        jsonPrinter = jsonPrinterFactory.createJsonPrinter();
        jsonPrinter.start();
    }

    public void close(boolean runErrors) {
        final String tmpFilename = jsonPrinterFactory.getFilename();
        try {
            jsonPrinter.close();
        } catch (IOException e) {
            log.error("JsonPrinter failed to close temporaryFile: {}, exception: {}",
                      tmpFilename,
                      e.getMessage());
            return;
        }
        if (!runErrors && overwriteErrorCount()) {
            renameOutputFile();
        } else {
            log.error("After processing exceptions, your incomplete results are available in {}",
                      tmpFilename);
        }
    }

    private void renameOutputFile() {
        final String tmpFilename = jsonPrinterFactory.getFilename();
        log.info("Attempt to rename tmpFile: '{}' to {}", tmpFilename, outputFileName);
        final Path tmpPath = Paths.get(tmpFilename);
        final Path destPath = Paths.get(outputFileName);
        try {
            Files.move(tmpPath, destPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Sucessfully renamed tmpFile: '{}' to {}", tmpFilename, outputFileName);
        } catch (IOException e) {
            log.error("Failed to rename tmpFile: '{}' to {}", tmpFilename, outputFileName);
            log.error("Output is available with in temporaryFile: {}", tmpFilename);
        }
    }

    /**
     * overwriteErrorCount()
     * <p>
     * When we first created the json file, we left room for an error count value.
     * Here, we get a series of characters to replace the placeholder that we
     * originally created and overlay that placeholder. THis avoids having to
     * read and completely re-write the file.
     */
    public boolean overwriteErrorCount() {
        final int errorCount = jsonPrinter.getErrorCount();
        final String replacementErrorCount = getPaddedErrorCount(errorCount);
        final String filename = jsonPrinterFactory.getFilename();
        try {
            RandomAccessFile writer = new RandomAccessFile(filename, "rw");
            final int errorCountOffset = jsonPrinter.getErrorCountOffset();
            writer.seek(errorCountOffset);
            final byte[] bytes = replacementErrorCount.getBytes();
            writer.write(bytes);
            writer.close();
            log.info("Successfully wrote errorCount of {} into {}", errorCount, filename);
            return true;
        } catch (Exception e) {
            log.info("Failed to write errorCount of {} into {}", errorCount, filename);
            return false;
        }
    }

    public String getPaddedErrorCount(int errorCount) {
        final int placeHolderLength = JsonPrinter.ERROR_COUNT_PLACEHOLDER.length();
        final String errorCountStr = Integer.toString(errorCount) + ',';
        final String replacementErrorCount = StringUtils.rightPad(errorCountStr, placeHolderLength, ' ');
        return replacementErrorCount;
    }


    public void addLogEntry(boolean hasError, LogEntry logEntry, int lineNumber) throws IOException {
        logEntryWindow.add(logEntry, lineNumber);
        if (hasError) {
            jsonPrinter.writeWindow(logEntryWindow);
        }
    }
}
