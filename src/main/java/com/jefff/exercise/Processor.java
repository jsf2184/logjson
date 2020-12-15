package com.jefff.exercise;

import com.jefff.exercise.io.input.LineReader;
import com.jefff.exercise.io.output.JsonPrinterFactory;
import com.jefff.exercise.io.output.OutputManager;
import com.jefff.exercise.parse.LogParser;
import com.jefff.exercise.parse.errors.ErrorDetector;
import com.jefff.exercise.utility.ArgParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * Hello world!
 */
@Slf4j
public class Processor {
    private int lineOffset;
    LineReader lineReader;
    LogParser logParser;
    ErrorDetector errorDetector;
    OutputManager outputManager;

    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);
        if (!argParser.process()) {
            return;
        }
        LineReader lineReader = new LineReader(argParser.getInputFileName());
        LogParser logParser = new LogParser();
        ErrorDetector errorDetector = new ErrorDetector();
        OutputManager outputManager = new OutputManager(argParser.getOutputFileName(),
                                                        new LogEntryWindow(),
                                                        new JsonPrinterFactory(createTempFileName()));

        Processor processor = new Processor(argParser.getLineOffset(),
                                            lineReader,
                                            logParser,
                                            errorDetector,
                                            outputManager);

        processor.run();
    }

    public Processor(int lineOffset,
                     LineReader lineReader,
                     LogParser logParser,
                     ErrorDetector errorDetector,
                     OutputManager outputManager) {
        this.lineOffset = lineOffset;
        this.lineReader = lineReader;
        this.logParser = logParser;
        this.errorDetector = errorDetector;
        this.outputManager = outputManager;
    }

    public void run() {
        try {
            lineReader.open();
        } catch (Exception e) {
            log.error("Failed to open lineReader: {}", e.getMessage());
            return;
        }
        try {
            outputManager.open();
        } catch (Exception e) {
            log.error("Failed to open outputManager: {}", e.getMessage());
            closeLineReader();
            return;
        }
        boolean runErrors = false;
        try {
            runLoop();
        } catch (Exception e) {
            log.error("Caught exception: '{}'  in runLoop ", e.getMessage());
            runErrors = true;
        } finally {
            closeLineReader();
            outputManager.close(runErrors);
        }
    }

    private void closeLineReader() {
        log.info("Attempt to closeLineReader()");
        try {
            lineReader.close();
            log.info("Successfully closed lineReader()");
        } catch (Exception e) {
            log.info("Caught exception closing lineReader(): {}", e.getMessage());
        }
    }

    private void runLoop() throws IOException {
        String line;
        int lineNumber = 0;
        while ((line = lineReader.readLine()) != null) {
            lineNumber++;
            if (lineNumber < lineOffset) {
                continue;
            }
            final LogEntry logEntry = logParser.parseLogEntry(line, lineNumber);
            if (logEntry == null) {
                continue;
            }
            final boolean hasErrors = errorDetector.hasErrors(logEntry.getMessage());
            outputManager.addLogEntry(hasErrors, logEntry, lineNumber);
        }

    }

    public static String createTempFileName() {
        final String result = "tmp." + UUID.randomUUID().toString() + ".json";
        return result;
    }

}
