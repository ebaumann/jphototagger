package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.domain.wordsets.Wordset;

/**
 * @author Elmar Baumann
 */
public final class WordsetUpdatedEvent {

    private final Object source;
    private final Wordset oldWordset;
    private final Wordset newWordset;

    public WordsetUpdatedEvent(Object source, Wordset oldWordset, Wordset newWordset) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (oldWordset == null) {
            throw new NullPointerException("oldWordset == null");
        }
        if (newWordset == null) {
            throw new NullPointerException("newWordset == null");
        }
        this.source = source;
        this.oldWordset = oldWordset;
        this.newWordset = newWordset;
    }

    public Object getSource() {
        return source;
    }

    public Wordset getOldWordset() {
        return oldWordset;
    }

    public Wordset getNewWordset() {
        return newWordset;
    }
}
