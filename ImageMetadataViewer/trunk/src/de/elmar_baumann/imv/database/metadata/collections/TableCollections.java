package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Table;

/**
 * Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-07
 */
public final class TableCollections extends Table {

    public static final TableCollections INSTANCE = new TableCollections();

    private TableCollections() {
        super("collections"); // NOI18N
    }

    @Override
    protected void addColumns() {
        addColumn(ColumnCollectionsIdFiles.INSTANCE);
        addColumn(ColumnCollectionsIdCollectionNames.INSTANCE);
        addColumn(ColumnCollectionsSequenceNumber.INSTANCE);
    }
}
