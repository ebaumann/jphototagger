package org.jphototagger.domain.text;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;

import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 *
 * @author Elmar Baumann
 */
public interface TextEntry {

    String getText();

    void setText(String text);

    void empty();

    MetaDataValue getMetaDataValue();

    void requestFocus();

    void setEditable(boolean editable);

    boolean isEditable();

    boolean isEmpty();

    void enableAutocomplete();

    boolean isDirty();

    void setDirty(boolean dirty);

    void addTextEntryListener(TextEntryListener listener);

    void removeTextEntryListener(TextEntryListener listener);

    /**
     * Returns all input components in order of their appearance, especially the
     * last component in the list is the last component in tab order.
     *
     * @return input components or empty list if the text entry has no input
     *         components
     */
    List<Component> getInputComponents();

    void addMouseListenerToInputComponents(MouseListener l);

    void removeMouseListenerFromInputComponents(MouseListener l);
}
