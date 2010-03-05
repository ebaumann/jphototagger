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
package de.elmar_baumann.jpt.database.metadata.selections;

/**
 * Hinweise für Spalten, die bearbeitet werden können.
 *
 * @author  Elmar Baumann
 * @version 2008-09-18
 */
public final class EditHints {

    private final boolean repeatable;
    private final SizeEditField sizeEditField;

    /**
     * Vorschlag: Größe des Editierfelds.
     */
    public enum SizeEditField {

        /**
         * Kleines Feld reicht aus (einzeilig)
         */
        SMALL,
        /**
         * "Mittleres" Feld reicht aus (etwa drei Zeilen)
         */
        MEDIUM,
        /**
         * Größeres Feld
         */
        LARGE
    }

    /**
     * Konstruktor.
     *
     * @param repeatable     true, wenn der Spaltenwert sich wiederholt
     * @param sizeEditField  Größenvorschlag
     */
    public EditHints(boolean repeatable, SizeEditField sizeEditField) {
        this.repeatable = repeatable;
        this.sizeEditField = sizeEditField;
    }

    /**
     * Liefert, ob der Spaltenwert sich wiederholt.
     *
     * @return true, wenn der Spaltenwert sich wiederholt
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * Liefert den Größenvorschlag.
     *
     * @return Größenvorschlag
     */
    public SizeEditField getSizeEditField() {
        return sizeEditField;
    }
}
