package com.jefff.exercise.parse.errors;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WordGenerator {

    /**
     * generateWords() - we take an input string and do a 2 phased split on that string into
     * component words. The first phase splits based on delimiters within the
     * string. The 2nd phase, takes the words produced by the first phase
     * and further splits them based on transitions between character types
     * <p>
     * example: "2applePies,,,,  goodAndSweet AND0Errors"
     * ->
     * ["2applePies", "2", "apple", "Pies",
     * "goodAndSweet", "good", "And", "Sweet",
     * "AND0Errors", "AND", "0", "Errors"
     * ]
     *
     * @param input - an input string
     * @return a List of words produced by the 2 phased split.
     */
    public List<String> generateWords(String input) {
        // first break our string into words based simply on our delimers
        final List<String> delimSplit = delimSplit(input);
        // And now use the transitions of characterTypes within the words to generate
        // even more words.

        final List<String> result = splitWordListOnTransitions(delimSplit);
        return result;
    }

    /**
     * delimSplit() - Do a preliminary delimter based split, splitting on any non-alphanumeric characters
     * Note that non alphanumeric characters are disposed of during the split.
     * <p>
     * example "abc3 22 def,# $ % g \t \n h 88" -> ["abc3", "22", "def", "g", "h", "88"]
     *
     * @param message - message to split
     * @return - resultan array of words
     */
    public static List<String> delimSplit(String message) {
        if (message == null) {
            return Collections.emptyList();
        }
        String[] strings = message.split("\\P{Alnum}+");
        final List<String> result = Arrays.stream(strings)
                                          .filter(s -> !StringUtils.isEmpty(s))
                                          .collect(Collectors.toList());
        return result;
    }


    /**
     * splitWordListOnTransitions - For each word on words list, attempt to split that
     * word into additional words, keying on transitions between upper and lower case
     * letters and digits.
     * <p>
     * The result is an ordered list containing a series of original words followed by its derivations
     * <p>
     * example: ["2applePies", "goodAndSweet"]
     * ->
     * ["2applePies"  "2" "apple", "Pies",
     * "goodAndSweet", "good", "And", "Sweet"
     * ]
     *
     * @param words - input list of words
     */
    public static List<String> splitWordListOnTransitions(List<String> words) {
        List<String> results = new ArrayList<>();
        words.forEach(w -> splitWordOnTransitions(w, results));
        return results;
    }

    /**
     * splitWordOnTransitions() - supplement our list of words with additional
     * words based on case-change splitting. For example the word: "inputException" results
     * in us adding "input" and "Exception" to the words list.
     *
     * @param word  - A word to try to split due to upper-lower case changes. The original
     *              word is already on the 'words' list, so it does not need to be placed there.
     * @param words - If 'word' can be split into 2 or more sub-words, place each sub-word on the
     *              words list
     */
    public static void splitWordOnTransitions(String word, List<String> words) {
        if (StringUtils.isEmpty(word)) {
            return;
        }
        // put the original on the list.
        words.add(word);

        StringBuilder wordBuf = new StringBuilder();
        int consecUppers = 0;
        int consecDigits = 0;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isDigit(c)) {
                if (consecDigits == 0 && wordBuf.length() > 0) {
                    words.add(wordBuf.toString());
                    wordBuf = new StringBuilder();
                }
                consecUppers = 0;
                consecDigits++;

            } else if (Character.isUpperCase(c)) {
                // We got an uppercase letter. It marks the start of a new word if we transitioned from
                // a lower case letter.
                //
                if (consecUppers == 0 && wordBuf.length() > 0) {
                    words.add(wordBuf.toString());
                    wordBuf = new StringBuilder();
                }
                consecUppers++;
                consecDigits = 0;
            } else {
                // We got a lower case letter. We need to figure out if its just a new letter on an existing word
                // or is it a transition to a new word? Examples...
                //   - EATmore   (the 'm' marks a new word) - criterion:  before the 'm', consecUppers > 1
                //   - eatMore   (the 'o' does not mark a new word) - criterion: before the 'o', consecUppers == 1
                //   - eatMore   (the 'r' does not mark a new work) - criterion: before the 'r', consecUppers == 0
                if (consecUppers > 1 || consecDigits > 0) {
                    // Something like 'EATmore" - the transition starts a new word, and we add 'EAT' to our list
                    words.add(wordBuf.toString());
                    wordBuf = new StringBuilder();
                }
                consecUppers = 0;
                consecDigits = 0;
            }
            // whether we started a new word, or we are just adding on to an existing word, add our latest char.
            wordBuf.append(c);
        }

        // Add the leftover provided it isn't the whole original word
        if (wordBuf.length() > 0) {
            String leftover = wordBuf.toString();
            if (!word.equals(leftover)) {
                words.add(leftover);
            }
        }
    }
}
