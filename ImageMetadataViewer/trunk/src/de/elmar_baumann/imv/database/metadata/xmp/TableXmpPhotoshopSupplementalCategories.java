package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class TableXmpPhotoshopSupplementalCategories extends Table {

    private static final TableXmpPhotoshopSupplementalCategories instance = new TableXmpPhotoshopSupplementalCategories();

    public static TableXmpPhotoshopSupplementalCategories getInstance() {
        return instance;
    }

    private TableXmpPhotoshopSupplementalCategories() {
        super("xmp_photoshop_supplementalcategories"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnXmpPhotoshopSupplementalCategoriesId.getInstance());
        addColumn(ColumnXmpPhotoshopSupplementalCategoriesIdXmp.getInstance());
        addColumn(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
    }
}
