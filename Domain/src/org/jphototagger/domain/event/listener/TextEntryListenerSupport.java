package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Support to add, remove and notify {@link TextEntryListener}s.
 *
 * @author Elmar Baumann
 */
public final class TextEntryListenerSupport extends ListenerSupport<TextEntryListener> {

    public void notifyTextRemoved(MetaDataValue metaDataValue, String removedText) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        if (removedText == null) {
            throw new NullPointerException("removedText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textRemoved(metaDataValue, removedText);
        }
    }

    public void notifyTextAdded(MetaDataValue metaDataValue, String addedText) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        if (addedText == null) {
            throw new NullPointerException("addedText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textAdded(metaDataValue, addedText);
        }
    }

    public void notifyTextChanged(MetaDataValue metaDataValue, String oldText, String newText) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        if (oldText == null) {
            throw new NullPointerException("oldText == null");
        }

        if (newText == null) {
            throw new NullPointerException("newText == null");
        }

        for (TextEntryListener listener : listeners) {
            listener.textChanged(metaDataValue, oldText, newText);
        }
    }
}
