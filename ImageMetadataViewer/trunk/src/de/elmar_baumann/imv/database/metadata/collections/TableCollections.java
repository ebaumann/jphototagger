package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public final class TableCollections extends Table {

    private static final TableCollections instance = new TableCollections();

    public static TableCollections getInstance() {
        return instance;
    }

    private TableCollections() {
        super("collections"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnCollectionsIdFiles.getInstance());
        addColumn(ColumnCollectionsIdCollectionNames.getInstance());
        addColumn(ColumnCollectionsSequenceNumber.getInstance());
    }
}
