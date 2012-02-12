package org.jphototagger.domain.repository.event.wordsets;

/**
 * @author Elmar Baumann
 */
public final class WordsetWordRemovedEvent {

    private final Object source;
    private final String wordsetName;
    private final String word;

    public WordsetWordRemovedEvent(Object source, String wordsetName, String word) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        if (word == null) {
            throw new NullPointerException("word == null");
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
