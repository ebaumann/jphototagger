package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import java.util.ArrayList;
import java.util.List;
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
