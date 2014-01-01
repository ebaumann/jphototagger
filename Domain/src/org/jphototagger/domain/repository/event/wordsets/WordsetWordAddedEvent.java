package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class WordsetWordAddedEvent {

    private final Object source;
    private final String wordsetName;
    private final String word;

    public WordsetWordAddedEvent(Object source, String wordsetName, String word) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (!StringUtil.hasContent(wordsetName)) {
            throw new IllegalArgumentException("Wordset name must have content: " + wordsetName);
        }
        if (!StringUtil.hasContent(word)) {
            throw new IllegalArgumentException("Word must have content: " + word);
        }
        this.source = source;
        this.wordsetName = wordsetName;
        this.word = word;
    }

    public Object getSource() {
        return source;
    }

    public String getWord() {
        return word;
    }

    public String getWordsetName() {
        return wordsetName;
    }
}
