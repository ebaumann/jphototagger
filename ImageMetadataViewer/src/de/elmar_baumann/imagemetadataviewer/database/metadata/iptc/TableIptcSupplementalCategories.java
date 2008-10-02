package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>iptc_supplemental_categories</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class TableIptcSupplementalCategories extends Table {

    private static TableIptcSupplementalCategories instance = new TableIptcSupplementalCategories();

    public static TableIptcSupplementalCategories getInstance() {
        return instance;
    }

    private TableIptcSupplementalCategories() {
        super("iptc_supplemental_categories"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnIptcSupplementalCategoriesId.getInstance());
        addColumn(ColumnIptcSupplementalCategoriesIdIptc.getInstance());
        addColumn(ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance());
    }
}
