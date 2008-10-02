package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpPhotoshopSupplementalCategoriesId extends Column {

    private static ColumnXmpPhotoshopSupplementalCategoriesId instance = new ColumnXmpPhotoshopSupplementalCategoriesId();

    public static ColumnXmpPhotoshopSupplementalCategoriesId getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopSupplementalCategoriesId() {
        super(
            TableXmpPhotoshopSupplementalCategories.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
