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

import de.elmar_baumann.jpt.database.metadata.Table;

/**
 * Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann
 * @version 2008-08-27
 */
public final class TableXmp extends Table {
    public static final TableXmp INSTANCE = new TableXmp();

    private TableXmp() {
        super("xmp");
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpDcCreator.INSTANCE);
        addColumn(ColumnXmpDcDescription.INSTANCE);
        addColumn(ColumnXmpDcRights.INSTANCE);
        addColumn(ColumnXmpDcTitle.INSTANCE);
        addColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        addColumn(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        addColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        addColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        addColumn(ColumnXmpPhotoshopCity.INSTANCE);
        addColumn(ColumnXmpPhotoshopCountry.INSTANCE);
        addColumn(ColumnXmpPhotoshopCredit.INSTANCE);
        addColumn(ColumnXmpPhotoshopHeadline.INSTANCE);
        addColumn(ColumnXmpPhotoshopInstructions.INSTANCE);
        addColumn(ColumnXmpPhotoshopSource.INSTANCE);
        addColumn(ColumnXmpPhotoshopState.INSTANCE);
        addColumn(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        addColumn(ColumnXmpRating.INSTANCE);
    }
}
