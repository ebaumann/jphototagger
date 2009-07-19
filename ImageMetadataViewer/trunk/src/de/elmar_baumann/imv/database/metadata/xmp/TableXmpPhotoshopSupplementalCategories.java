package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class TableXmpPhotoshopSupplementalCategories extends Table {

    public static final TableXmpPhotoshopSupplementalCategories INSTANCE = new TableXmpPhotoshopSupplementalCategories();

    private TableXmpPhotoshopSupplementalCategories() {
        super("xmp_photoshop_supplementalcategories"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpPhotoshopSupplementalCategoriesId.INSTANCE);
        addColumn(ColumnXmpPhotoshopSupplementalCategoriesIdXmp.INSTANCE);
        addColumn(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
    }
}
