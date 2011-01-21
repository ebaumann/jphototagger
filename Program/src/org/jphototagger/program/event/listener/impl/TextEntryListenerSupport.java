package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.event.listener.TextEntryListener;

/**
 * Support to add, remove and notify {@link TextEntryListener}s.
 *
 * @author Elmar Baumann
 */
public final class TextEntryListenerSupport
        extends ListenerSupport<TextEntryListener> {
    public void notifyTextRemoved(Column column, String removedText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (removedText == null) {
            throw new NullPointerException("removedText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textRemoved(column, removedText);
        }
    }

    public void notifyTextAdded(Column column, String addedText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (addedText == null) {
            throw new NullPointerException("addedText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textAdded(column, addedText);
        }
    }

    public void notifyTextChanged(Column column, String oldText,
                                  String newText) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (oldText == null) {
            throw new NullPointerException("oldText == null");
        }

        if (newText == null) {
            throw new NullPointerException("newText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textChanged(column, oldText, newText);
        }
    }
}
