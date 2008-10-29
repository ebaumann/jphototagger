package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Text entry with text and column.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/29
 */
public class TextEntryContent implements TextEntry {

    private String text;
    private Column column;

    public TextEntryContent(String text, Column column) {
        this.text = text;
        this.column = column;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    /**
     * Does nothing.
     */
    @Override
    public void focus() {
    }

    /**
     * Does nothing.
     */
    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    /**
     * Does nothing.
     */
    @Override
    public void setAutocomplete() {
    }

    /**
     * Returns always false.
     * 
     * @return false
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * Does nothing.
     * 
     * @param dirty  see interface documentation
     */
    @Override
    public void setDirty(boolean dirty) {
    }

    @Override
    public TextEntry clone() {
        return new TextEntryContent(text, column);
    }
}
