package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id_iptc</code> der Tabelle <code>iptc_supplemental_categories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnIptcSupplementalCategoriesIdIptc extends Column {

    private static ColumnIptcSupplementalCategoriesIdIptc instance = new ColumnIptcSupplementalCategoriesIdIptc();

    public static ColumnIptcSupplementalCategoriesIdIptc getInstance() {
        return instance;
    }

    private ColumnIptcSupplementalCategoriesIdIptc() {
        super(
            TableIptcSupplementalCategories.getInstance(),
            "id_iptc", // NOI18N
            DataType.integer);

        setCanBeNull(false);
        setReferences(ColumnIptcId.getInstance());

    }
}
