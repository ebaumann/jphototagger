package org.jphototagger.domain.text;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Text entry with text and metadata value.
 *
 * @author Elmar Baumann
 */
public final class TextEntryContent implements TextEntry {

    private String text;
    private MetaDataValue metaDataValue;

    public TextEntryContent(String text, MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        this.text = text;
        this.metaDataValue = metaDataValue;
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
    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
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
    public void setEditable(boolean editable) {
    }

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
        return metaDataValue + "=" + text;
    }
}
