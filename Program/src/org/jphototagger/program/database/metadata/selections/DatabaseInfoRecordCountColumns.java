package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.exif.ColumnExifLens;
import org.jphototagger.domain.database.exif.ColumnExifRecordingEquipment;
import org.jphototagger.domain.database.file.ColumnFilesFilename;
import org.jphototagger.domain.database.xmp.ColumnXmpDcCreator;
import org.jphototagger.domain.database.xmp.ColumnXmpDcRights;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseInfoRecordCountColumns {
    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {
        COLUMNS.add(ColumnFilesFilename.INSTANCE);
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCity.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopState.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCountry.INSTANCE);
        COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCredit.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);
        COLUMNS.add(ColumnExifLens.INSTANCE);
    }

    public static List<Column> get() {
        return new ArrayList<Column>(COLUMNS);
    }

    private DatabaseInfoRecordCountColumns() {}
}
