/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.database.metadata.Column;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Text as value of a {@link Column}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-18
 */
public interface TextEntry {

    public String getText();

    public void setText(String text);

    /**
     * Empties the text.
     *
     * @param dirty true if set dirty, else fals
     */
    public void empty(boolean dirty);

    public Column getColumn();

    /**
     * Requests the focus to the text input field.
     */
    public void focus();

    public void setEditable(boolean editable);

    public boolean isEditable();

    public boolean isEmpty();

    /**
     * Enables autocomplete.
     */
    public void setAutocomplete();

    /**
     * Returns whether the text has been changed since the last call
     * to {@link #setText(java.lang.String)}.
     *
     * @return true if changed
     */
    public boolean isDirty();

    /**
     * Sets how the entry shall behave like changes since last call to
     * {@link #setText(java.lang.String)}.
     *
     * @param dirty  true if the text was changed
     */
    public void setDirty(boolean dirty);

    /**
     * Returns all input components in order of their appearance, especially the
     * last component in the list is the last component in tab order.
     *
     * @return input components or empty list if the text entry has no input
     *         components
     */
    public List<Component> getInputComponents();

    public void addMouseListenerToInputComponents(MouseListener l);

    public void removeMouseListenerFromInputComponents(MouseListener l);
}
