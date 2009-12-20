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
package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifDateTimeOriginal;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import java.util.ArrayList;
import java.util.List;

/**
 * Spalten für die erweiterte Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class AdvancedSearchColumns {

    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCategory.INSTANCE);
        COLUMNS.add(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCity.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopState.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCountry.INSTANCE);
        COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCredit.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopInstructions.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        COLUMNS.add(ColumnXmpRating.INSTANCE);
        COLUMNS.add(ColumnExifDateTimeOriginal.INSTANCE);
        COLUMNS.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS.add(ColumnExifIsoSpeedRatings.INSTANCE);
        COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);
        COLUMNS.add(ColumnFilesFilename.INSTANCE);
    }

    /**
     * Liefert die Spalten für die erweiterte Suche.
     * 
     * @return Suchspalten
     */
    public static List<Column> get() {
        return new ArrayList<Column>(COLUMNS);
    }

    private AdvancedSearchColumns() {
    }
}
