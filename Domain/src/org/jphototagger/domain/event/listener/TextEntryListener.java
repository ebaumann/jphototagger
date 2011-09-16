package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 *
 * @author Elmar Baumann
 */
public interface TextEntryListener {

    /**
     * Text was removed from a repeatable text entry.
     *
     * @param metaDataValue
     * @param removedText removed text
     */
    void textRemoved(MetaDataValue metaDataValue, String removedText);

    /**
     * Text was added to a repeatable text entry.
     *
     * @param metaDataValue
     * @param addedText added text
     */
    void textAdded(MetaDataValue metaDataValue, String addedText);

    /**
     * Text has been changed.
     *
     * @param metaDataValue
     * @param oldText old text
     * @param newText new text
     */
    void textChanged(MetaDataValue metaDataValue, String oldText, String newText);
}
