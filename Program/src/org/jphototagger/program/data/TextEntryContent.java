package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;

import java.awt.Component;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Text entry with text and column.
 *
 * @author Elmar Baumann
 */
public final class TextEntryContent implements TextEntry {
    private String text;
    private Column column;

    public TextEntryContent(String text, Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

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

        // ignore
    }

    /**
     * Does nothing.
     *
     * @param editable true if editable
     */
    @Override
    public void setEditable(boolean editable) {}

    @Override
    public boolean isEmpty() {
        return (text == null) || text.isEmpty();
    }

    /**
     * Does nothing.
     */
    @Override
    public void setAutocomplete() {

        // ignore
    }

    /**
     * Returns false.
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

        // ignore
    }

    /**
     * Returns false.
     *
     * @return false
     */
    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void empty(boolean dirty) {
        text = "";

        // ignore dirty
    }

    @Override
    public List<Component> getInputComponents() {
        return new ArrayList<Component>();
    }

    @Override
    public void addMouseListenerToInputComponents(MouseListener l) {

        // ignore
    }

    @Override
    public void removeMouseListenerFromInputComponents(MouseListener l) {

        // ignore
    }

    @Override
    public String toString() {
        return column + "=" + text;
    }

}
