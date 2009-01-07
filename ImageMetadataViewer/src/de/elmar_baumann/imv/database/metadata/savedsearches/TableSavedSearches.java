package de.elmar_baumann.imv.database.metadata.savedsearches;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>saved_searches</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public final class TableSavedSearches extends Table {

    private static final TableSavedSearches instance = new TableSavedSearches();

    private TableSavedSearches() {
        super("saved_searches"); // NOI18N
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static TableSavedSearches getInstance() {
        return instance;
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnSavedSearchesName.getInstance());
    }
}
