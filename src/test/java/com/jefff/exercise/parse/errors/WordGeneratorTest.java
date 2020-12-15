package com.jefff.exercise.parse.errors;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordGeneratorTest {
    WordGenerator wordGenerator = new WordGenerator();

    @Test
    public void testGenerateWords() {
        runGenerateWords(null, Collections.emptyList());
        runGenerateWords("", Collections.emptyList());
        runGenerateWords("2applePies,,,,  goodAndSweet AND0Errors abc",
                         Arrays.asList(
                                 "2applePies", "2", "apple", "Pies",
                                 "goodAndSweet", "good", "And", "Sweet",
                                 "AND0Errors", "AND", "0", "Errors",
                                 "abc"
                         ));
    }

    public void runGenerateWords(String input, List<String> expected) {
        final List<String> words = wordGenerator.generateWords(input);
        Assert.assertEquals(expected, words);
    }

    @Test
    public void testDelimSplit() {
        runDelimSplit(null, Collections.emptyList());
        runDelimSplit("", Collections.emptyList());
        runDelimSplit("abc3 22 def,# $ % g \t \n h 88",
                      Arrays.asList("abc3", "22", "def", "g", "h", "88"));
    }


    @Test
    public void testSplitWordOnTransitions() {
        runSplitWordOnTransitions(null, Collections.emptyList());
        runSplitWordOnTransitions("", Collections.emptyList());
        runSplitWordOnTransitions("0", Collections.singletonList("0"));
        runSplitWordOnTransitions("000", Collections.singletonList("000"));
        runSplitWordOnTransitions("a", Collections.singletonList("a"));
        runSplitWordOnTransitions("aaa", Collections.singletonList("aaa"));
        runSplitWordOnTransitions("A", Collections.singletonList("A"));
        runSplitWordOnTransitions("AAA", Collections.singletonList("AAA"));
        runSplitWordOnTransitions("theGoodDOGisGoingHomeTODAY",
                                  Arrays.asList("theGoodDOGisGoingHomeTODAY", "the", "Good", "DOG", "is", "Going", "Home", "TODAY"));
        runSplitWordOnTransitions("0aA00aaAAabcAbc0",
                                  Arrays.asList("0aA00aaAAabcAbc0", "0", "a", "A", "00", "aa", "AA", "abc", "Abc", "0"));
        runSplitWordOnTransitions("THEcowJumpedOVERtheMoonI",
                                  Arrays.asList("THEcowJumpedOVERtheMoonI", "THE", "cow", "Jumped", "OVER", "the", "Moon", "I"));
        runSplitWordOnTransitions("00THE1cow22Jumped0OVERtheMoonI3",
                                  Arrays.asList("00THE1cow22Jumped0OVERtheMoonI3", "00", "THE", "1", "cow", "22", "Jumped", "0", "OVER", "the", "Moon", "I", "3"));
    }

    @Test
    public void testSplitWordListOnTransitions() {
        // Running on a list of 3 input strings should produce q result that has the split words derived from each of the
        // originals (plus the originals)

        runSplitWordListOnTransitions(Arrays.asList("theGoodDOGisGoingHomeTODAY",
                                                    "00THE1cow22Jumped0OVERtheMoonI3",
                                                    "simple"),
                                      Arrays.asList("theGoodDOGisGoingHomeTODAY", "the", "Good", "DOG", "is", "Going", "Home", "TODAY",
                                                    "00THE1cow22Jumped0OVERtheMoonI3", "00", "THE", "1", "cow", "22", "Jumped", "0", "OVER", "the", "Moon", "I", "3",
                                                    "simple"));
    }

    public void runDelimSplit(String input, List<String> expected) {
        final List<String> words = WordGenerator.delimSplit(input);
        Assert.assertEquals(expected, words);
    }

    public void runSplitWordOnTransitions(String input, List<String> expected) {
        List<String> words = new ArrayList<>();
        WordGenerator.splitWordOnTransitions(input, words);
        Assert.assertEquals(expected, words);
    }

    public void runSplitWordListOnTransitions(List<String> words, List<String> expected) {
        final List<String> result = WordGenerator.splitWordListOnTransitions(words);
        Assert.assertEquals(expected, result);
    }


}