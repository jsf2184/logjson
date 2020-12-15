package com.jefff.exercise.utility;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ArgParser {

    public static final String DEFAULT_INPUT_FILE = "application.log";
    public static final String DEFAULT_OUTPUT_FILE = "errors.json";
    public static final int DEFAULT_LINE_OFFSET = 0;
    private String outputFileName;
    private String inputFileName;
    private int lineOffset;
    String[] args;


    public ArgParser(String[] args) {
        this.args = args;
        outputFileName = DEFAULT_OUTPUT_FILE;
        inputFileName = DEFAULT_INPUT_FILE;
        lineOffset = DEFAULT_LINE_OFFSET;
    }

    public boolean process() {
        int numArgs = args.length;
        for (int i = 0; i < numArgs; i++) {
            String arg = args[i];
            switch (arg) {
                case "-h":
                    printUsage();
                    return false;

                case "-o":
                    if (i + 1 >= numArgs) {
                        System.err.println("Missing -o argument");
                        printUsage();
                        return false;
                    }
                    outputFileName = args[++i];
                    break;

                case "-i":
                    if (i + 1 >= numArgs) {
                        System.err.println("Missing -i argument");
                        printUsage();
                        return false;
                    }
                    inputFileName = args[++i];
                    break;

                case "-n":
                    if (i + 1 >= numArgs) {
                        System.err.println("Missing -n argument");
                        printUsage();
                        return false;
                    }
                    Integer value = getInteger(args[++i]);
                    if (value == null) {
                        System.err.printf("Invalid -n argument: %s\n", args[i]);
                        printUsage();
                        return false;
                    }
                    lineOffset = value;
                    break;

                default:
                    System.err.printf("Unexpected argument: %s\n", arg);
                    System.err.println("Extra command line arguments.");
                    printUsage();
                    return false;
            }
        }

        log.info("ArgumentSummary");
        log.info("  inputFileName  = {}", inputFileName);
        log.info("  outputFileName = {}", outputFileName);
        log.info("  lineOffset     = {}", lineOffset);
        log.info("");
        return true;
    }

    public static Integer getInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return null;
        }
    }


    public String getOutputFileName() {
        // for now, we don't give the user to control the outputFileName, maybe later.
        return outputFileName;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    private void printUsage() {
        System.err.println("Usage: |-i inputFileName| |-o outputFileName| |-n lineOffset| |-h|");
        System.err.printf("  inputFileName:     default = %s\n", DEFAULT_INPUT_FILE);
        System.err.printf("  outputFileName:    default = %s\n", DEFAULT_OUTPUT_FILE);
        System.err.printf("  lineOffset:        default = %d\n", DEFAULT_LINE_OFFSET);
    }

}
