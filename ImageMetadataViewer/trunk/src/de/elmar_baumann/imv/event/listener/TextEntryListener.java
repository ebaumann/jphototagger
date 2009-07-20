package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Listens to an {@link de.elmar_baumann.imv.data.TextEntry}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-20
 */
public interface TextEntryListener {

    /**
     * Text was removed from a repeatable text entry.
     *
     * @param column      column
     * @param removedText removed text
     */
    public void textRemoved(Column column, String removedText);

    /**
     * Text was added to a repeatable text entry.
     *
     * @param column    column
     * @param addedText added text
     */
    public void textAdded(Column column, String addedText);

    /**
     * Text has been changed.
     *
     * @param column  column
     * @param oldText old text
     * @param newText new text
     */
    public void textChanged(Column column, String oldText, String newText);
}
