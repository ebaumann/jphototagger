package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>supplementalcategory</code> der Tabelle <code>xmp_photoshop_supplementalcategories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory
    extends Column {

    private static ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory instance =
        new ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory() {
        super(
            TableXmpPhotoshopSupplementalCategories.getInstance(),
            "supplementalcategory", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.Description"));
    }
}
