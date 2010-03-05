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
package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;

/**
 * Ereignis bei
 * {@link de.elmar_baumann.jpt.view.panels.EditMetadataPanels}.
 *
 * @author  Elmar Baumann
 * @version 2008-09-22
 */
public final class EditMetadataPanelsEvent {

    public enum Type {

        /**
         * Bearbeiten wurde ermöglict
         */
        EDIT_ENABLED,
        /**
         * Bearbeiten wurde deaktiviert
         */
        EDIT_DISABLED
    }

    private final EditMetadataPanels source;
    private final Type type;

    /**
     * Konstruktor.
     *
     * @param source  Ereignisquelle
     * @param type    Ereignistyp
     */
    public EditMetadataPanelsEvent(EditMetadataPanels source, Type type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Liefert die Ereignisquelle.
     *
     * @return Ereignisquelle
     */
    public EditMetadataPanels getSource() {
        return source;
    }

    /**
     * Liefert den Ereignistyp.
     *
     * @return Ereignistyp
     */
    public Type getType() {
        return type;
    }
}
