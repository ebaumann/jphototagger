package org.jphototagger.domain.metadata.mapping;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.column.ColumnXmpDcCreator;
import org.jphototagger.domain.database.column.ColumnXmpDcDescription;
import org.jphototagger.domain.database.column.ColumnXmpDcRights;
import org.jphototagger.domain.database.column.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.column.ColumnXmpDcTitle;
import org.jphototagger.domain.database.column.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.column.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.column.ColumnXmpLastModified;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Returns, whether a XMP column has repeatable values.
 *
 * @author Elmar Baumann
 */
public final class XmpRepeatableValues {

    private static final Map<Column, Boolean> IS_REPEATABLE = new HashMap<Column, Boolean>();

    static {
        IS_REPEATABLE.put(ColumnXmpDcCreator.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcDescription.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcRights.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcSubjectsSubject.INSTANCE, true);
        IS_REPEATABLE.put(ColumnXmpDcTitle.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCity.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCountry.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCredit.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopHeadline.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopInstructions.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopSource.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopState.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpLastModified.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpRating.INSTANCE, false);
    }

    /**
     * Returns, whether a XMP column has repeatable values.
     *
     * @param  xmpColumn  XMP column
     * @return true if the column contains repeatable values
     * @throws IllegalArgumentException if there is no information whether
     *         the column has repeatable values
     */
    public static boolean isRepeatable(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        Boolean repeatable = IS_REPEATABLE.get(xmpColumn);

        if (repeatable == null) {
            throw new IllegalArgumentException("Unknown column: " + xmpColumn);
        }

        return repeatable;
    }

    private XmpRepeatableValues() {
    }
}
