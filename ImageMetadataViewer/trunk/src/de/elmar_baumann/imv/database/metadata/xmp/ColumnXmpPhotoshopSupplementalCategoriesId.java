package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpPhotoshopSupplementalCategoriesId extends Column {

    public static final ColumnXmpPhotoshopSupplementalCategoriesId INSTANCE = new ColumnXmpPhotoshopSupplementalCategoriesId();

    private ColumnXmpPhotoshopSupplementalCategoriesId() {
        super(
            TableXmpPhotoshopSupplementalCategories.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
