/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Text entry with text and column.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-29
 */
public final class TextEntryContent implements TextEntry {

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
        // ignore
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

    @Override
    public TextEntry clone() {
        return new TextEntryContent(text, column);
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
        text = ""; // NOI18N
        // ignore dirty
    }
}
