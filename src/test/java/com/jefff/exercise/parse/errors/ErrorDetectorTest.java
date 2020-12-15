package com.jefff.exercise.parse.errors;

import org.junit.Assert;
import org.junit.Test;

public class ErrorDetectorTest {

    ErrorDetector errorDetector;
    WordGenerator wordGenerator;
    KeyWordMatcher keyWordMatcher;

    @Test
    public void hasErrorTests() {
        errorDetector = new ErrorDetector();
        runTest("got arrors", true);
        runTest("errors found ", true);
        runTest("0 errors found ", false);
        runTest("020 errors found ", true);
        runTest("got exceptins", true);
        runTest("caught nullExceptins", true);
        runTest("caught no nullExceptins ", false);
        runTest("caught 00 nullExceptins", false);
        runTest("caught no nullExceptins but caught another ioException", true);
        runTest("caught no nullExceptins but found 3 other errs", true);
    }

    private void runTest(String message, boolean expected) {
        boolean result = errorDetector.hasErrors(message);
        Assert.assertEquals(expected, result);
    }
}