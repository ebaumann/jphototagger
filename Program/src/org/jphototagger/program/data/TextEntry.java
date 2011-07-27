package org.jphototagger.program.data;

import org.jphototagger.domain.Column;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Text as value of a {@link Column}.
 *
 * @author Elmar Baumann
 */
public interface TextEntry {
    String getText();

    void setText(String text);

    /**
     * Empties the text.
     *
     * @param dirty true if set dirty, else fals
     */
    void empty(boolean dirty);

    Column getColumn();

    /**
     * Requests the focus to the text input field.
     */
    void focus();

    void setEditable(boolean editable);

    boolean isEditable();

    boolean isEmpty();

    /**
     * Enables autocomplete.
     */
    void setAutocomplete();

    /**
     * Returns whether the text has been changed since the last call
     * to {@link #setText(java.lang.String)}.
     *
     * @return true if changed
     */
    boolean isDirty();

    /**
     * Sets how the entry shall behave like changes since last call to
     * {@link #setText(java.lang.String)}.
     *
     * @param dirty  true if the text was changed
     */
    void setDirty(boolean dirty);

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
