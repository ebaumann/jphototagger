package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>iptc_supplemental_categories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcSupplementalCategoriesId extends Column {

    private static ColumnIptcSupplementalCategoriesId instance = new ColumnIptcSupplementalCategoriesId();

    public static ColumnIptcSupplementalCategoriesId getInstance() {
        return instance;
    }

    private ColumnIptcSupplementalCategoriesId() {
        super(
            TableIptcSupplementalCategories.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
