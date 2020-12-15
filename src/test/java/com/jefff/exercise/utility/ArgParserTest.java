package com.jefff.exercise.utility;

import org.junit.Assert;
import org.junit.Test;

public class ArgParserTest {

    public static final String OVERWRITE_OUTPUT_FILE = "myOut.json";
    public static final String OVERWRITE_INPUT_FILE = "myInput.log";
    public static final String OVERWRITE_LINE_OFFSET = "10";

    @Test
    public void testProcess() {
        // test no args defaulting
        runParseTest(new String[0],
                     ArgParser.DEFAULT_INPUT_FILE,
                     ArgParser.DEFAULT_OUTPUT_FILE,
                     ArgParser.DEFAULT_LINE_OFFSET,
                     true);

        // test with all args specifed
        runParseTest(new String[]{
                             "-i", OVERWRITE_INPUT_FILE,
                             "-o", OVERWRITE_OUTPUT_FILE,
                             "-n", OVERWRITE_LINE_OFFSET},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     true);

        // test with -h
        runParseTest(new String[]{
                             "-i", OVERWRITE_INPUT_FILE,
                             "-o", OVERWRITE_OUTPUT_FILE,
                             "-n", OVERWRITE_LINE_OFFSET,
                             "-h"},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

        // test with extra arg
        runParseTest(new String[]{
                             "-i", OVERWRITE_INPUT_FILE,
                             "-o", OVERWRITE_OUTPUT_FILE,
                             "-n", OVERWRITE_LINE_OFFSET,
                             "-extra"},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

        // test with bad integer
        runParseTest(new String[]{
                             "-i", OVERWRITE_INPUT_FILE,
                             "-o", OVERWRITE_OUTPUT_FILE,
                             "-n", "abc",
                     },
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

        // test with dangling argument
        runParseTest(new String[]{"-n"},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

        // test with dangling argument
        runParseTest(new String[]{"-o"},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

        // test with dangling argument
        runParseTest(new String[]{"-i"},
                     OVERWRITE_INPUT_FILE,
                     OVERWRITE_OUTPUT_FILE,
                     Integer.parseInt(OVERWRITE_LINE_OFFSET),
                     false);

    }

    void runParseTest(String[] args,
                      String expectedInputFileName,
                      String expectedOutputFileName,
                      int expectedLineOffset,
                      boolean expectSuccess) {
        ArgParser argParser = new ArgParser(args);
        Assert.assertEquals(expectSuccess, argParser.process());
        if (expectSuccess) {
            Assert.assertEquals(expectedInputFileName, argParser.getInputFileName());
            Assert.assertEquals(expectedOutputFileName, argParser.getOutputFileName());
            Assert.assertEquals(expectedLineOffset, argParser.getLineOffset());
        }
    }

}