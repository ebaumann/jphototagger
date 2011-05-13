package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import java.util.ArrayList;
import java.util.List;

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

    private XmpColumns() {}

    public static List<Column> get() {
        return new ArrayList<Column>(XMP_COLUMNS);
    }
}
