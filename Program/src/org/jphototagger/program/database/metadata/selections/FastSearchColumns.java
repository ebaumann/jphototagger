package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.database.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.domain.database.column.ColumnXmpDcDescription;
import org.jphototagger.domain.database.column.ColumnXmpDcRights;
import org.jphototagger.domain.database.column.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.column.ColumnXmpDcTitle;
import org.jphototagger.domain.database.column.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopHeadline;
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
