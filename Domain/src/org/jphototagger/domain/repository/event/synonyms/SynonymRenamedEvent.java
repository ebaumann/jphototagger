package org.jphototagger.domain.repository.event.synonyms;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class SynonymRenamedEvent {

    private final Object source;
    private final String oldSynonymName;
    private final String newSynonymName;

    public SynonymRenamedEvent(Object source, String oldSynonymName, String newSynonymName) {
        if (oldSynonymName == null) {
            throw new NullPointerException("oldSynonymName == null");
        }
        
        if (newSynonymName == null) {
            throw new NullPointerException("newSynonymName == null");
        }
        
        this.source = source;
        this.oldSynonymName = oldSynonymName;
        this.newSynonymName = newSynonymName;
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
