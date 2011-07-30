package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.exif.ColumnExifFocalLength;
import org.jphototagger.domain.database.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.domain.database.exif.ColumnExifRecordingEquipment;
import org.jphototagger.domain.database.file.ColumnFilesFilename;
import org.jphototagger.domain.database.xmp.ColumnXmpDcDescription;
import org.jphototagger.domain.database.xmp.ColumnXmpDcRights;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.ColumnXmpDcTitle;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopHeadline;
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
