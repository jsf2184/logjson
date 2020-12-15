package com.jefff.exercise.parse.errors;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * KeywordEnum
 * <p>
 * A KeywordEnum can either be an "error" synonym or a "negator". This enum
 * has a few smarts that allow it to easily/efficiently search the special words
 * for a match. It does not have the smarts to do "one-off" searches. For example,
 * it can't match up "excepion" with "exception". That is handled by the
 * 'ErrorAnalyzer' class which does use this enum to first try the simple searches.
 */
public enum KeyWordEnum {

    // These first few are negators. Consider a log message that
    // said
    //    'no errors found"
    // the presence of the negator "NO" helps us realize that this message does
    // not constitue an error.
    //
    NO(true, "no", 0, false),
    ZERO(true, "zero", 0, false),
    NUMZERO(true, "0", 0, false),

    // Here are the enums that are basically "error" synonyms
    FAIL(false, "fail", 0, true),
    FAILED(false, "failed", 1, false),
    FAILURE(false, "failure", 1, true),
    ERR(false, "err", 0, true),
    ERROR(false, "error", 1, true),
    INCORRECT(false, "incorrect", 2, false),
    INVALID(false, "invalid", 2, false),
    EXCEPTION(false, "exception", 2, true);

    KeyWordEnum(boolean isNegator, String matchingString, int tolerance, boolean allowPlurals) {
        this.isNegator = isNegator;
        this.matchingString = matchingString;
        this.tolerance = tolerance;
        this.allowPlurals = allowPlurals;
    }

    static Map<String, KeyWordEnum> wordMap;
    static int maxTolerance = 0;

    static {
        wordMap = new HashMap<>();
        for (KeyWordEnum wordEnum : KeyWordEnum.values()) {
            wordMap.put(wordEnum.matchingString, wordEnum);
            maxTolerance = Math.max(maxTolerance, wordEnum.tolerance);
        }
    }

    public static int getMaxTolerance() {
        return maxTolerance;
    }

    public boolean isNegator() {
        return isNegator;
    }

    public int getTolerance() {
        return tolerance;
    }

    public String getMatchingString() {
        return matchingString;
    }

    public boolean allowPlurals() {
        return allowPlurals;
    }

    public static KeyWordEnum find(String word) {
        String preppedWord = prepForInitialSearch(word);
        KeyWordEnum result = wordMap.get(preppedWord);
        if (result != null) {
            return result;
        }
        // if we make the word singular
        String singular = convertToSingular(preppedWord);
        if (singular == null) {
            return null;
        }
        result = wordMap.get(singular);
        return result != null && result.allowPlurals ? result : null;
    }

    public static String prepForInitialSearch(String word) {
        if (StringUtils.isEmpty(word)) {
            return "";
        }
        // treat number "words" specially
        if (Character.isDigit(word.charAt(0))) {
            String result = word.replaceAll("^0+", "");
            if (result.length() == 0) {
                return "0";
            }
            return result;
        }
        return word.toLowerCase();
    }

    /**
     * convertToSingular() - If the input 'word' ends with 's', remove the 's', otherwise
     * it is not plural and return null.
     *
     * @param word - input
     * @return - output as described above.
     */
    public static String convertToSingular(String word) {
        if (word == null) {
            return null;
        }
        if (word.endsWith("s")) {
            return (word.substring(0, word.length() - 1));
        }
        return null;
    }

    boolean isNegator;
    String matchingString;
    private int tolerance;
    private boolean allowPlurals;

}
