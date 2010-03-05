/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.event.listener;

import de.elmar_baumann.jpt.database.metadata.Column;

/**
 * Listens to an {@link de.elmar_baumann.jpt.data.TextEntry}.
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
