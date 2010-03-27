/*
 * @(#)ComboBoxModelLogfileFormatter.java    Created on 2008-09-17
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

package org.jphototagger.program.model;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are the <strong>{@link Class} objects</strong> of specialized
 * {@link java.util.logging.Formatter}s.
 *
 * These formatters can be used for formatting {@link java.util.logging.Logger}
 * output.
 *
 * @author  Elmar Baumann
 */
public final class ComboBoxModelLogfileFormatter extends DefaultComboBoxModel {
    private static final long serialVersionUID = -7817194934431355197L;

    public ComboBoxModelLogfileFormatter() {
        addElements();
    }

    private void addElements() {

        // Wird die Reihenfolge verändert, stimmen die Benutzereinstellungen
        // nicht mehr, da diese den Index speichern
        addElement(XMLFormatter.class);
        addElement(SimpleFormatter.class);
    }
}
