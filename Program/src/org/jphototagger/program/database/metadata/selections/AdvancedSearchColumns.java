package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.database.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifDateTimeOriginal;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifLens;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.domain.database.column.ColumnXmpDcCreator;
import org.jphototagger.domain.database.column.ColumnXmpDcDescription;
import org.jphototagger.domain.database.column.ColumnXmpDcRights;
import org.jphototagger.domain.database.column.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.column.ColumnXmpDcTitle;
import org.jphototagger.domain.database.column.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.column.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopHeadline;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopInstructions;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopState;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.domain.database.column.ColumnXmpRating;
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
