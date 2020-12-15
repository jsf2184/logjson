package com.jefff.exercise.parse.errors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class KeyWordMatcher {

    SimpleMatcher simpleMatcher;
    ApproximateMatcher approximateMatcher;

    public KeyWordMatcher() {
        this.simpleMatcher = new SimpleMatcher();
        this.approximateMatcher = new ApproximateMatcher();
    }

    public KeyWordEnum matchWord(String word) {
        KeyWordEnum result = simpleMatcher.matchWord(word);
        if (result == null) {
            result = approximateMatcher.matchWord(word);
        }
        return result;
    }

    public static class ApproximateMatcher {

        LevenshteinDistance[] distanceCalculators;

        public ApproximateMatcher() {
            // Allocate distance calculators specified by our KeywordEnums
            distanceCalculators = new LevenshteinDistance[KeyWordEnum.getMaxTolerance() + 1];
            for (KeyWordEnum wordEnum : KeyWordEnum.values()) {
                final int tolerance = wordEnum.getTolerance();
                if (tolerance > 0 && distanceCalculators[tolerance] == null) {
                    // first time we've encountered an enum with this level of tolerance.
                    distanceCalculators[tolerance] = new LevenshteinDistance(tolerance);
                }
            }
        }

        public KeyWordEnum matchWord(String word) {
            // We might potentially have to run a match against ell of our KeywordEnums

            final String preppedWord = KeyWordEnum.prepForInitialSearch(word);
            final String singularWord = KeyWordEnum.convertToSingular(preppedWord);

            for (KeyWordEnum wordEnum : KeyWordEnum.values()) {
                final LevenshteinDistance calculator = getCalculator(wordEnum.getTolerance());
                if (calculator == null) {
                    continue;
                }
                final String matchingWord = selectWordForMatching(preppedWord, singularWord, wordEnum);
                final Integer result = calculator.apply(wordEnum.getMatchingString(), matchingWord);
                if (result != null && result >= 0) {
                    return wordEnum;
                }
            }
            return null;
        }

        LevenshteinDistance getCalculator(int tolerance) {
            if (tolerance < distanceCalculators.length && tolerance > 0) {
                return distanceCalculators[tolerance];
            }
            return null;
        }

        /**
         * selectWordForMatching()
         * <p>
         * Some interesting logic here. Supposed we have the word "errors" and we want to know if
         * it matches KeywordEnum.ERROR. Of course, intuitively it makes sense that we'd want to
         * allow this match. In this example
         * preppedWord would be "errors"
         * singularWord would be "error"
         * wordEnum is KeywordEnum.ERROR
         * Because KeywordEnum.ERROR accepts plurals, then we have the okay to use the converted
         * singularWord which would be 'error' and would match.
         *
         * @param preppedWord  - the word with simple preparation (e.g. case lowering)
         * @param singularWord - if the word is plural (ends with an 's'), the word without the 's'
         * @param wordEnum     - The wordEnum we are going to try matching with (to know whether plurals make sense)
         * @return - the selection (preppedWord or singularWord) to use for the distanceMatch.
         */
        public static String selectWordForMatching(String preppedWord, String singularWord, KeyWordEnum wordEnum) {
            return (wordEnum.allowPlurals() && !StringUtils.isEmpty(singularWord))
                    ? singularWord
                    : preppedWord;
        }

    }

    public static class SimpleMatcher {

        public KeyWordEnum matchWord(String word) {
            // Hopefully we have a simple match based on a simple enum string search
            return KeyWordEnum.find(word);
        }
    }
}
