package de.elmar_baumann.imv.database.metadata.savedsearches;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>saved_searches</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-17
 */
public final class TableSavedSearches extends Table {

    public static final TableSavedSearches INSTANCE = new TableSavedSearches();

    private TableSavedSearches() {
        super("saved_searches"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnSavedSearchesName.INSTANCE);
    }
}
