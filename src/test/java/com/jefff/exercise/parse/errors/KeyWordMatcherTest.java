package com.jefff.exercise.parse.errors;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class KeyWordMatcherTest {

    KeyWordMatcher.SimpleMatcher simpleMatcher = new KeyWordMatcher.SimpleMatcher();
    KeyWordMatcher.ApproximateMatcher approximateMatcher = new KeyWordMatcher.ApproximateMatcher();

    @Test
    public void testSimpleMatch() {
        final List<KeyWordExpectation> expectationList = KeyWordExpectation.getExpectationList();
        expectationList.forEach(exp -> verifySimpleMatch(exp.getInput(), exp.getExpected()));
    }

    @Test
    public void testApproximateMatch() {
        // tolerance for the 'a' in error
        verifyApproximateMatch("ArroR", KeyWordEnum.ERROR);
        // and the plural exemption even thought it would only have tolerance for one correction.
        verifyApproximateMatch("arroRs", KeyWordEnum.ERROR);

        // FAILD has tolerance for 1 correction, but not for plurals
        verifyApproximateMatch("FAILD", KeyWordEnum.FAILED);
        verifyApproximateMatch("FAILDS", null); // but no plurals

        // EXCEPTION has tolerance for 2 corrections and plurals
        verifyApproximateMatch("Exceptin", KeyWordEnum.EXCEPTION);
        verifyApproximateMatch("eXceptn", KeyWordEnum.EXCEPTION);
        verifyApproximateMatch("eXceptns", KeyWordEnum.EXCEPTION);
        verifyApproximateMatch("except", null);  // but not 3
    }

    void verifySimpleMatch(String word, KeyWordEnum expected) {
        final KeyWordEnum wordEnum = simpleMatcher.matchWord(word);
        Assert.assertEquals(expected, wordEnum);
    }

    void verifyApproximateMatch(String word, KeyWordEnum expected) {
        final KeyWordEnum wordEnum = approximateMatcher.matchWord(word);
        Assert.assertEquals(expected, wordEnum);
    }

    public static class KeyWordExpectation {
        String input;
        KeyWordEnum expected;

        public KeyWordExpectation(String input, KeyWordEnum expected) {
            this.input = input;
            this.expected = expected;
        }

        public String getInput() {
            return input;
        }

        public KeyWordEnum getExpected() {
            return expected;
        }

        public static List<KeyWordExpectation> getExpectationList() {
            return Arrays.asList(
                    new KeyWordExpectation("no", KeyWordEnum.NO),
                    new KeyWordExpectation("failed", KeyWordEnum.FAILED),
                    new KeyWordExpectation("incorrect", KeyWordEnum.INCORRECT),
                    new KeyWordExpectation("error", KeyWordEnum.ERROR),

                    // some simple tests where case adjustment does the trick.
                    new KeyWordExpectation("NO", KeyWordEnum.NO),
                    new KeyWordExpectation("fAILEd", KeyWordEnum.FAILED),
                    new KeyWordExpectation("INCORRECT", KeyWordEnum.INCORRECT),
                    new KeyWordExpectation("eRRor", KeyWordEnum.ERROR),

                    // special treatment for number zero
                    new KeyWordExpectation("0", KeyWordEnum.NUMZERO),
                    new KeyWordExpectation("00000", KeyWordEnum.NUMZERO),

                    // tests that succeed because of special plural allowances
                    new KeyWordExpectation("eRRors", KeyWordEnum.ERROR),
                    new KeyWordExpectation("failures", KeyWordEnum.FAILURE),
                    new KeyWordExpectation("excepTIONS", KeyWordEnum.EXCEPTION),

                    // tests that fail because there is no special plural allowance
                    new KeyWordExpectation("INCORRECTS", null),
                    new KeyWordExpectation("invalids", null),

                    // And a bunch that just don't match anything
                    new KeyWordExpectation(null, null),
                    new KeyWordExpectation("", null),
                    new KeyWordExpectation("abc", null),
                    new KeyWordExpectation("00009", null),
                    new KeyWordExpectation("9", null)
            );
        }

    }
}