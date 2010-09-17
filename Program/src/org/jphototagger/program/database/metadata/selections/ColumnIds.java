/*
 * @(#)ColumnIds.java    Created on 2008-09-13
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
import org.jphototagger.program.database.metadata.exif.ColumnExifDateTimeOriginal;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifLens;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.file.ColumnFilesLastModified;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * IDs der Tabellenspalten, die für den Benutzer relevant sind. So kann in der
 * Datenbank eine ID abgespeichert werden, aus der eindeutig die Tabellenspalte
 * ermittelt werden kann.
 *
 * @author Elmar Baumann
 */
public final class ColumnIds {
    private static final Map<Integer, Column> COLUMN_OF_ID =
        new HashMap<Integer, Column>();
    private static final Map<Column, Integer> ID_OF_COLUMN =
        new HashMap<Column, Integer>();

    static {

        // UPDATE IF an new Column was created an will be used by a class that
        // uses this class.
        // *Never* change existing IDs and don't use an ID twice!
        COLUMN_OF_ID.put(0, ColumnExifDateTimeOriginal.INSTANCE);
        COLUMN_OF_ID.put(1, ColumnExifFocalLength.INSTANCE);
        COLUMN_OF_ID.put(2, ColumnExifIsoSpeedRatings.INSTANCE);
        COLUMN_OF_ID.put(3, ColumnExifRecordingEquipment.INSTANCE);
        COLUMN_OF_ID.put(4, ColumnFilesFilename.INSTANCE);
        COLUMN_OF_ID.put(5, ColumnFilesLastModified.INSTANCE);

        // Removed 6: ColumnFilesThumbnail
        COLUMN_OF_ID.put(7, ColumnXmpDcDescription.INSTANCE);
        COLUMN_OF_ID.put(8, ColumnXmpDcRights.INSTANCE);
        COLUMN_OF_ID.put(9, ColumnXmpDcTitle.INSTANCE);

        // Removed 10: ColumnXmpIptc4xmpcoreCountrycode
        COLUMN_OF_ID.put(11, ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMN_OF_ID.put(12, ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        COLUMN_OF_ID.put(13, ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        COLUMN_OF_ID.put(15, ColumnXmpPhotoshopCity.INSTANCE);
        COLUMN_OF_ID.put(16, ColumnXmpPhotoshopCountry.INSTANCE);
        COLUMN_OF_ID.put(17, ColumnXmpPhotoshopCredit.INSTANCE);
        COLUMN_OF_ID.put(18, ColumnXmpPhotoshopHeadline.INSTANCE);
        COLUMN_OF_ID.put(19, ColumnXmpPhotoshopInstructions.INSTANCE);
        COLUMN_OF_ID.put(20, ColumnXmpPhotoshopSource.INSTANCE);
        COLUMN_OF_ID.put(21, ColumnXmpPhotoshopState.INSTANCE);
        COLUMN_OF_ID.put(22, ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        COLUMN_OF_ID.put(23, ColumnXmpDcCreator.INSTANCE);
        COLUMN_OF_ID.put(24, ColumnXmpDcSubjectsSubject.INSTANCE);

        // Removed 26: ColumnCollectionnamesName
        // Removed 27: ColumnSavedSearchesName
        COLUMN_OF_ID.put(28, ColumnXmpRating.INSTANCE);
        COLUMN_OF_ID.put(29, ColumnExifLens.INSTANCE);
        COLUMN_OF_ID.put(30, ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);

        // Next ID: 31 - UPDATE ID after assigning! --
        Set<Integer> keys = COLUMN_OF_ID.keySet();

        for (Integer key : keys) {
            ID_OF_COLUMN.put(COLUMN_OF_ID.get(key), key);
        }
    }

    private ColumnIds() {}

    /**
     * Liefert eine Spalte mit bestimmter ID.
     *
     * @param  id ID
     * @return Spalte oder null bei ungültiger ID
     */
    public static Column getColumn(int id) {
        return COLUMN_OF_ID.get(id);
    }

    /**
     * Liefert die ID einer Spalte.
     *
     * @param  column Spalte
     * @return ID
     */
    public static int getId(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return ID_OF_COLUMN.get(column);
    }
}
