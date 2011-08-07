package org.jphototagger.domain.repository.event.synonyms;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class SynonymOfWordDeletedEvent {

    private final Object source;
    private final String word;
    private final String synonym;

    public SynonymOfWordDeletedEvent(Object source, String word, String synonym) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        
        if (synonym == null) {
            throw new NullPointerException("synonym == null");
        }
        
        this.source = source;
        this.word = word;
        this.synonym = synonym;
    }

    public Object getSource() {
        return source;
    }

    public String getSynonym() {
        return synonym;
    }

    public String getWord() {
        return word;
    }
}
