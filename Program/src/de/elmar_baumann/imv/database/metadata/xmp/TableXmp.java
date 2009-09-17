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
package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class TableXmp extends Table {

    public static final TableXmp INSTANCE = new TableXmp();

    private TableXmp() {
        super("xmp"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpId.INSTANCE);
        addColumn(ColumnXmpIdFiles.INSTANCE);
        addColumn(ColumnXmpDcCreator.INSTANCE);
        addColumn(ColumnXmpDcDescription.INSTANCE);
        addColumn(ColumnXmpDcRights.INSTANCE);
        addColumn(ColumnXmpDcTitle.INSTANCE);
        addColumn(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
        addColumn(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        addColumn(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        addColumn(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        addColumn(ColumnXmpPhotoshopCategory.INSTANCE);
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
