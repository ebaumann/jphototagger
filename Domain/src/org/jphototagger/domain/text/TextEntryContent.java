package org.jphototagger.domain.text;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Text entry with text and metadata value.
 *
 * @author Elmar Baumann
 */
public final class TextEntryContent implements TextEntry {

    private String text;
    private final MetaDataValue metaDataValue;

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
    public void requestFocus() {
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
    public void enableAutocomplete() {
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
    public void empty() {
        text = "";
    }

    @Override
    public String toString() {
        return metaDataValue + "=" + text;
    }

    @Override
    public List<Component> getInputComponents() {
        return new ArrayList<>();
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
    public void addTextEntryListener(TextEntryListener listener) {
        // ignore
    }

    @Override
    public void removeTextEntryListener(TextEntryListener listener) {
        // ignore
    }

    @Override
    public Collection<? extends Component> getExcludeFromAutoMnemonicComponents() {
        return Collections.emptyList();
    }
}
