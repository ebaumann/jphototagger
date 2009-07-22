package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.listener.TextEntryListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Support to add, remove and notify {@link TextEntryListener}s.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/20
 */
public final class TextEntryListenerSupport {

    private final Set<TextEntryListener> listeners =
            Collections.synchronizedSet(new HashSet<TextEntryListener>());

    public void addTextEntryListener(TextEntryListener listener) {
        listeners.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        listeners.remove(listener);
    }

    public void notifyTextRemoved(Column column, String removedText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textRemoved(column, removedText);
            }
        }
    }

    public void notifyTextAdded(Column column, String addedText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textAdded(column, addedText);
            }
        }
    }

    public void notifyTextChanged(Column column, String oldText, String newText) {
        synchronized (listeners) {
            for (TextEntryListener listener : listeners) {
                listener.textChanged(column, oldText, newText);
            }
        }
    }
}
