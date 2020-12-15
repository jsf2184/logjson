package com.jefff.exercise.parse.errors;

import java.util.List;

public class ErrorDetector {
    WordGenerator wordGenerator;
    KeyWordMatcher keyWordMatcher;

    // Constructor for dependency injection
    public ErrorDetector(WordGenerator wordGenerator, KeyWordMatcher keyWordMatcher) {
        this.wordGenerator = wordGenerator;
        this.keyWordMatcher = keyWordMatcher;
    }

    // Constructor for convenience
    public ErrorDetector() {
        this(new WordGenerator(), new KeyWordMatcher());
    }

    public boolean hasErrors(String message) {
        List<String> words = wordGenerator.generateWords(message);
        boolean hasNegator = false;
        for (String word : words) {
            KeyWordEnum keyWordEnum = keyWordMatcher.matchWord(word);
            if (keyWordEnum == null) {
                continue;
            }
            if (keyWordEnum.isNegator()) {
                hasNegator = true;
            } else {
                // We found an error
                if (!hasNegator) {
                    // found a match and no active negator. This is an error!
                    return true;
                }
                // this error match neutralizes our previous negator
                hasNegator = false;
            }
        }
        return false;
    }
}
