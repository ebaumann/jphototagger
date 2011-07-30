package org.jphototagger.domain.database.search;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.exif.ColumnExifDateTimeOriginal;
import org.jphototagger.domain.database.exif.ColumnExifFocalLength;
import org.jphototagger.domain.database.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.domain.database.exif.ColumnExifLens;
import org.jphototagger.domain.database.exif.ColumnExifRecordingEquipment;
import org.jphototagger.domain.database.file.ColumnFilesFilename;
import org.jphototagger.domain.database.xmp.ColumnXmpDcCreator;
import org.jphototagger.domain.database.xmp.ColumnXmpDcDescription;
import org.jphototagger.domain.database.xmp.ColumnXmpDcRights;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.ColumnXmpDcTitle;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.domain.database.xmp.ColumnXmpRating;
import java.util.ArrayList;
import java.util.List;

/**
 * Spalten für die erweiterte Suche.
 *
 * @author Elmar Baumann
 */
public final class AdvancedSearchColumns {
    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
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
        COLUMNS.add(ColumnExifLens.INSTANCE);
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

    private AdvancedSearchColumns() {}
}
