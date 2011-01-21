package org.jphototagger.program.event.listener;

import org.jphototagger.program.database.metadata.Column;

/**
 * Listens to an {@link org.jphototagger.program.data.TextEntry}.
 *
 * @author Elmar Baumann
 */
public interface TextEntryListener {

    /**
     * Text was removed from a repeatable text entry.
     *
     * @param column      column
     * @param removedText removed text
     */
    void textRemoved(Column column, String removedText);

    /**
     * Text was added to a repeatable text entry.
     *
     * @param column    column
     * @param addedText added text
     */
    void textAdded(Column column, String addedText);

    /**
     * Text has been changed.
     *
     * @param column  column
     * @param oldText old text
     * @param newText new text
     */
    void textChanged(Column column, String oldText, String newText);
}
