package org.jphototagger.domain.metadata.mapping;

import org.jphototagger.domain.database.Column;
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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmar Baumann
 */
public final class XmpColumnXmpArrayNameMapping {

    private static final Map<Column, String> XMP_ARRAY_NAME_OF_COLUMN = new HashMap<Column, String>();

    static {
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, "dc:creator");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE, "dc:description");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, "dc:rights");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE, "dc:subject");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, "dc:title");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, "Iptc4xmpCore:DateCreated");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, "Iptc4xmpCore:Location");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, "photoshop:AuthorsPosition");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, "photoshop:CaptionWriter");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE, "photoshop:City");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE, "photoshop:Country");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE, "photoshop:Credit");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE, "photoshop:Headline");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE, "photoshop:Instructions");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE, "photoshop:Source");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE, "photoshop:State");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE,
                "photoshop:TransmissionReference");
        XMP_ARRAY_NAME_OF_COLUMN.put(ColumnXmpRating.INSTANCE, "xap:Rating");
    }

    /**
     *
     * @param  column XMP column
     * @return        array name or null
     */
    public static String getXmpArrayNameOfColumn(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return XMP_ARRAY_NAME_OF_COLUMN.get(column);
    }

    /**
     * Finds a column of a string with a specific path start.
     *
     * @param stringArrayNameStart string with a array name start, can contain
     *                             more characters after name start
     * @return                     column or null if not found
     */
    public static Column findColumn(String stringArrayNameStart) {
        if (stringArrayNameStart == null) {
            throw new NullPointerException("stringArrayNameStart == null");
        }

        for (Column column : XMP_ARRAY_NAME_OF_COLUMN.keySet()) {
            if (stringArrayNameStart.startsWith(XMP_ARRAY_NAME_OF_COLUMN.get(column))) {
                return column;
            }
        }

        return null;
    }

    private XmpColumnXmpArrayNameMapping() {
    }
}
