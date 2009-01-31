package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnXmpPhotoshopSupplementalCategoriesIdXmp extends Column {

    private static final ColumnXmpPhotoshopSupplementalCategoriesIdXmp instance = new ColumnXmpPhotoshopSupplementalCategoriesIdXmp();

    public static ColumnXmpPhotoshopSupplementalCategoriesIdXmp getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopSupplementalCategoriesIdXmp() {
        super(
            TableXmpPhotoshopSupplementalCategories.getInstance(),
            "id_xmp", // NOI18N
            DataType.BIGINT);

        setCanBeNull(false);
        setReferences(ColumnXmpId.getInstance());
    }
}
