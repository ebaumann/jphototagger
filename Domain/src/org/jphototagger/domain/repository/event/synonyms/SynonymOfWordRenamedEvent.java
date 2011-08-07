package org.jphototagger.domain.repository.event.synonyms;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class SynonymOfWordRenamedEvent {
    private final Object source;
    private final String word;
    private final String oldSynonymName;
    private final String newSynonymName;

    public SynonymOfWordRenamedEvent(Object source, String word, String oldSynonymName, String newSynonymName) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        
        if (oldSynonymName == null) {
            throw new NullPointerException("oldSynonymName == null");
        }
        
        if (newSynonymName == null) {
            throw new NullPointerException("newSynonymName == null");
        }
        
        this.source = source;
        this.word = word;
        this.oldSynonymName = oldSynonymName;
        this.newSynonymName = newSynonymName;
    }

    public String getWord() {
        return word;
    }

    public String getNewSynonymName() {
        return newSynonymName;
    }

    public String getOldSynonymName() {
        return oldSynonymName;
    }

    public Object getSource() {
        return source;
    }
}
