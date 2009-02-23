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
 * @version 2009/02/20
 */
public final class XmpRepeatableValues {

    private static final Map<Column, Boolean> repeatableOf = new HashMap<Column, Boolean>();

    static {
        repeatableOf.put(ColumnXmpDcCreator.INSTANCE, false);
        repeatableOf.put(ColumnXmpDcDescription.INSTANCE, false);
        repeatableOf.put(ColumnXmpDcRights.INSTANCE, false);
        repeatableOf.put(ColumnXmpDcSubjectsSubject.INSTANCE, true);
        repeatableOf.put(ColumnXmpDcTitle.INSTANCE, false);
        repeatableOf.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, false);
        repeatableOf.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopCategory.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopCity.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopCountry.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopCredit.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopHeadline.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopInstructions.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopSource.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopState.INSTANCE, false);
        repeatableOf.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, true);
        repeatableOf.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, false);
        repeatableOf.put(ColumnXmpLastModified.INSTANCE, false);
    }

    /**
     * Returns, whether a XMP column has repeatable values.

     * @param  xmpColumn  XMP column
     * @return true if the column contains repeatable values
     * @throws IllegalArgumentException if there is no information whether
     *         the column has repeatable values
     */
    public static boolean isRepeatable(Column xmpColumn) {
        Boolean repeatable = repeatableOf.get(xmpColumn);
        if (repeatable == null)
            throw new IllegalArgumentException("Unknown column: " + xmpColumn);
        return repeatable;
    }

    private XmpRepeatableValues() {
    }
}
