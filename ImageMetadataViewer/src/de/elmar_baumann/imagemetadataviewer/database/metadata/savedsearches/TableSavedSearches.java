package de.elmar_baumann.imagemetadataviewer.database.metadata.savedsearches;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>saved_searches</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public class TableSavedSearches extends Table {

    private static TableSavedSearches instance = new TableSavedSearches();

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
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnSavedSearchesName.getInstance());
    }
}
