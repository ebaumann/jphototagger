package de.elmar_baumann.imagemetadataviewer.database.metadata.collections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;

/**
 * Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public class TableCollections extends Table {

    private static TableCollections instance = new TableCollections();

    public static TableCollections getInstance() {
        return instance;
    }

    private TableCollections() {
        super("collections"); // NOI18N
    }

    @Override
    protected void addColumns() {
        // Reihenfolge NIE verändern, siehe de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        addColumn(ColumnCollectionsIdFiles.getInstance());
        addColumn(ColumnCollectionsIdCollectionNames.getInstance());
        addColumn(ColumnCollectionsSequenceNumber.getInstance());
    }
}
