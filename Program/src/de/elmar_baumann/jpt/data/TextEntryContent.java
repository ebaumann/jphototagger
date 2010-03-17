/*
 * @(#)TextEntryContent.java    2008-10-29
 *
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

import java.util.ArrayList;
import java.util.List;

/**
 * Text entry with text and column.
 *
 * @author  Elmar Baumann
 */
public final class TextEntryContent implements TextEntry {
    private String text;
    private Column column;

    public TextEntryContent(String text, Column column) {
        this.text   = text;
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
}
