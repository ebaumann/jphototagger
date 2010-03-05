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

package de.elmar_baumann.jpt.database.metadata.xmp;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Column.DataType;
import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Spalte <code>photoshop_state</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann
 * @version 2008-08-23
 */
public final class ColumnXmpPhotoshopState extends Column {
    public static final ColumnXmpPhotoshopState INSTANCE =
        new ColumnXmpPhotoshopState();

    private ColumnXmpPhotoshopState() {
        super(TableXmp.INSTANCE, "photoshop_state", DataType.STRING);
        setLength(32);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopState.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopState.LongerDescription"));
    }
}
