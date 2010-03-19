/*
 * @(#)ColumnXmpPhotoshopInstructions.java    Created on 2008-08-23
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

package de.elmar_baumann.jpt.database.metadata.xmp;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Column.DataType;
import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann
 */
public final class ColumnXmpPhotoshopInstructions extends Column {
    public static final ColumnXmpPhotoshopInstructions INSTANCE =
        new ColumnXmpPhotoshopInstructions();

    private ColumnXmpPhotoshopInstructions() {
        super("photoshop_instructions", "xmp", DataType.STRING);
        setLength(256);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopInstructions.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopInstructions.LongerDescription"));
    }
}
