package de.elmar_baumann.imv.database.metadata.mapping;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns, whether a XMP column has repeatable values.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-20
 */
public final class XmpRepeatableValues {

    private static final Map<Column, Boolean> IS_REPEATABLE =
            new HashMap<Column, Boolean>();

    static {
        IS_REPEATABLE.put(ColumnXmpDcCreator.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcDescription.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcRights.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpDcSubjectsSubject.INSTANCE, true);
        IS_REPEATABLE.put(ColumnXmpDcTitle.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCategory.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCity.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCountry.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCredit.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopHeadline.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopInstructions.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopSource.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopState.INSTANCE, false);
        IS_REPEATABLE.put(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE,
                true);
        IS_REPEATABLE.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE,
                false);
        IS_REPEATABLE.put(ColumnXmpLastModified.INSTANCE, false);
    }

    /**
     * Returns, whether a XMP column has repeatable values.

     * @param  xmpColumn  XMP column
     * @return true if the column contains repeatable values
     * @throws IllegalArgumentException if there is no information whether
     *         the column has repeatable values
     */
    public static boolean isRepeatable(Column xmpColumn) {
        Boolean repeatable = IS_REPEATABLE.get(xmpColumn);
        if (repeatable == null)
            throw new IllegalArgumentException("Unknown column: " + xmpColumn); // NOI18N
        return repeatable;
    }

    private XmpRepeatableValues() {
    }
}
