package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>collection_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public class TableCollectionNames extends Table {

    private static TableCollectionNames instance = new TableCollectionNames();

    public static TableCollectionNames getInstance() {
        return instance;
    }

    private TableCollectionNames() {
        super("collection_names"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imv.database.metadata.selections.AllTables.get()
        addColumn(ColumnCollectionnamesId.getInstance());
        addColumn(ColumnCollectionnamesName.getInstance());
    }
}
