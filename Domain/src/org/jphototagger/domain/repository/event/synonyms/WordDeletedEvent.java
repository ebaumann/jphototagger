package org.jphototagger.domain.repository.event.synonyms;

/**
 * @author  Elmar Baumann
 */
public final class WordDeletedEvent {

    private final Object source;
    private final String word;

    public WordDeletedEvent(Object source, String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }

        this.source = source;
        this.word = word;
    }

    public Object getSource() {
        return source;
    }

    public String getWord() {
        return word;
    }

}
