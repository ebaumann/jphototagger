/*
 * @(#)FastSearchColumns.java    Created on 2008-07-29
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

package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;

import java.util.ArrayList;
import java.util.List;

/**
 * Spalten für die Schnellsuche.
 *
 * @author Elmar Baumann
 */
public final class FastSearchColumns {
    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {

        // XMP
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpDcRights.INSTANCE);

        // EXIF
        COLUMNS.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS.add(ColumnExifIsoSpeedRatings.INSTANCE);
        COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);

        // Files
        COLUMNS.add(ColumnFilesFilename.INSTANCE);
    }

    public static List<Column> get() {
        return new ArrayList<Column>(COLUMNS);
    }

    private FastSearchColumns() {}
}
