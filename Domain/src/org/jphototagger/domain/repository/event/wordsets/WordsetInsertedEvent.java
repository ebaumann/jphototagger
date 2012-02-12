package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
public final class WordsetInsertedEvent {

    private final Object source;
    private final Wordset wordset;

    public WordsetInsertedEvent(Object source, Wordset wordset) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (wordset == null) {
            throw new NullPointerException("wordset == null");
        }
        this.source = source;
        this.wordset = wordset;
    }

    public Object getSource() {
        return source;
    }

    public Wordset getWordset() {
        return wordset;
    }
}
