package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifLens;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
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
