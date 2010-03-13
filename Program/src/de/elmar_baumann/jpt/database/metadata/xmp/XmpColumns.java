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

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of all XMP columns.
 *
 * @author  Elmar Baumann
 * @version 2010-01-08
 */
public final class XmpColumns {
    private static final List<Column> XMP_COLUMNS = new ArrayList<Column>();

    static {
        XMP_COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCity.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCountry.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCredit.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopInstructions.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopState.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpRating.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    private XmpColumns() {}

    public static List<Column> get() {
        return new ArrayList<Column>(XMP_COLUMNS);
    }
}
