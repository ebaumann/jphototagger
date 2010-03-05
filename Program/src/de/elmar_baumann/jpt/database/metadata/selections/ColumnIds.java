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

import de.elmar_baumann.jpt.database.metadata.collections
    .ColumnCollectionnamesName;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifDateTimeOriginal;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifLens;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesThumbnail;
import de.elmar_baumann.jpt.database.metadata.savedsearches
    .ColumnSavedSearchesName;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * IDs der Tabellenspalten, die für den Benutzer relevant sind. So kann in der
 * Datenbank eine ID abgespeichert werden, aus der eindeutig die Tabellenspalte
 * ermittelt werden kann.
 *
 * @author  Elmar Baumann
 * @version 2008-09-13
 */
public final class ColumnIds {
    private static final Map<Integer, Column> COLUMN_OF_ID =
        new HashMap<Integer, Column>();
    private static final Map<Column, Integer> ID_OF_COLUMN =
        new HashMap<Column, Integer>();

    static {

        // TODO PERMANENT: Add new relevant (e. g. searchable) columns
        // Never change existing IDs, don't use an ID twice!
        COLUMN_OF_ID.put(0, ColumnExifDateTimeOriginal.INSTANCE);
        COLUMN_OF_ID.put(1, ColumnExifFocalLength.INSTANCE);
        COLUMN_OF_ID.put(2, ColumnExifIsoSpeedRatings.INSTANCE);
        COLUMN_OF_ID.put(3, ColumnExifRecordingEquipment.INSTANCE);
        COLUMN_OF_ID.put(4, ColumnFilesFilename.INSTANCE);
        COLUMN_OF_ID.put(5, ColumnFilesLastModified.INSTANCE);
        COLUMN_OF_ID.put(6, ColumnFilesThumbnail.INSTANCE);
        COLUMN_OF_ID.put(7, ColumnXmpDcDescription.INSTANCE);
        COLUMN_OF_ID.put(8, ColumnXmpDcRights.INSTANCE);
        COLUMN_OF_ID.put(9, ColumnXmpDcTitle.INSTANCE);
        COLUMN_OF_ID.put(10, ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
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
        COLUMN_OF_ID.put(26, ColumnCollectionnamesName.INSTANCE);
        COLUMN_OF_ID.put(27, ColumnSavedSearchesName.INSTANCE);
        COLUMN_OF_ID.put(28, ColumnXmpRating.INSTANCE);
        COLUMN_OF_ID.put(29, ColumnExifLens.INSTANCE);
        COLUMN_OF_ID.put(30, ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);

        // Next ID: 31 - UPDATE ID after assigning! --

        Set<Integer> keys = COLUMN_OF_ID.keySet();

        for (Integer key : keys) {
            ID_OF_COLUMN.put(COLUMN_OF_ID.get(key), key);
        }
    }

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
        return ID_OF_COLUMN.get(column);
    }

    private ColumnIds() {}
}
