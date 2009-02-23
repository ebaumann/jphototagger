package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>collection_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public final class TableCollectionNames extends Table {

    public static final TableCollectionNames INSTANCE = new TableCollectionNames();

    private TableCollectionNames() {
        super("collection_names"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnCollectionnamesId.INSTANCE);
        addColumn(ColumnCollectionnamesName.INSTANCE);
    }
}
