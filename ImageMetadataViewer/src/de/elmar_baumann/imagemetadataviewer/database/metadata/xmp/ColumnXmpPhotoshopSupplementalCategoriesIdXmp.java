package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_xmp</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnXmpPhotoshopSupplementalCategoriesIdXmp extends Column {

    private static ColumnXmpPhotoshopSupplementalCategoriesIdXmp instance = new ColumnXmpPhotoshopSupplementalCategoriesIdXmp();

    public static ColumnXmpPhotoshopSupplementalCategoriesIdXmp getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopSupplementalCategoriesIdXmp() {
        super(
            TableXmpPhotoshopSupplementalCategories.getInstance(),
            "id_xmp", // NOI18N
            DataType.Bigint);

        setCanBeNull(false);
        setReferences(ColumnXmpId.getInstance());
    }
}
