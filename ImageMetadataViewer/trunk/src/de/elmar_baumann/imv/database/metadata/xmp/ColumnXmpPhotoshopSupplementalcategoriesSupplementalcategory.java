package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>supplementalcategory</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory
    extends Column {

    public static final ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory INSTANCE =
        new ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory();

    private ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory() {
        super(
            TableXmpPhotoshopSupplementalCategories.INSTANCE,
            "supplementalcategory", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.LongerDescription")); // NOI18N
    }
}
