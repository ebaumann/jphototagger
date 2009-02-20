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
        repeatableOf.put(ColumnXmpDcCreator.getInstance(), false);
        repeatableOf.put(ColumnXmpDcDescription.getInstance(), false);
        repeatableOf.put(ColumnXmpDcRights.getInstance(), false);
        repeatableOf.put(ColumnXmpDcSubjectsSubject.getInstance(), true);
        repeatableOf.put(ColumnXmpDcTitle.getInstance(), false);
        repeatableOf.put(ColumnXmpIptc4xmpcoreCountrycode.getInstance(), false);
        repeatableOf.put(ColumnXmpIptc4xmpcoreLocation.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopAuthorsposition.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopCaptionwriter.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopCategory.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopCity.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopCountry.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopCredit.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopHeadline.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopInstructions.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopSource.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopState.getInstance(), false);
        repeatableOf.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), true);
        repeatableOf.put(ColumnXmpPhotoshopTransmissionReference.getInstance(), false);
        repeatableOf.put(ColumnXmpLastModified.getInstance(), false);
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
