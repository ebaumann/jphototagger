package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Collection of all XMP columns.
 *
 * @author Elmar Baumann
 */
public final class XmpColumns {

    private static final List<Column> XMP_COLUMNS = new ArrayList<Column>();

    static {
        XMP_COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCity.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCountry.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopCredit.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopInstructions.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopState.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpRating.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    private XmpColumns() {
    }

    public static List<Column> get() {
        return new ArrayList<Column>(XMP_COLUMNS);
    }
}
