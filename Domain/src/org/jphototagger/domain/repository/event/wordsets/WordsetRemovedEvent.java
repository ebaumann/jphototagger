package org.jphototagger.domain.repository.event.wordsets;

/**
 * @author Elmar Baumann
 */
public final class WordsetRemovedEvent {

    private final Object source;
    private final String wordsetName;

    public WordsetRemovedEvent(Object source, String wordsetName) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (wordsetName == null) {
            throw new NullPointerException("wordsetName == null");
        }
        this.source = source;
        this.wordsetName = wordsetName;
    }

    public Object getSource() {
        return source;
    }

    public String getWordset() {
        return wordsetName;
    }
}
