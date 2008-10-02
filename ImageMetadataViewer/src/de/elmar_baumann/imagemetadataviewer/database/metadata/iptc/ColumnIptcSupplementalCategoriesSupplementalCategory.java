package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>supplemental_category</code> der Tabelle <code>iptc_supplemental_categories</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcSupplementalCategoriesSupplementalCategory extends Column {

    private static ColumnIptcSupplementalCategoriesSupplementalCategory instance =
        new ColumnIptcSupplementalCategoriesSupplementalCategory();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcSupplementalCategoriesSupplementalCategory() {
        super(
            TableIptcSupplementalCategories.getInstance(),
            "supplemental_category", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcSupplementalCategoriesSupplementalCategory.Description"));
    }
}
