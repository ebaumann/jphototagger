package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;

/**
 * Spalte <code>id_collectionnnames</code> der Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public final class ColumnCollectionsIdCollectionNames extends Column {

    private static final ColumnCollectionsIdCollectionNames instance = new ColumnCollectionsIdCollectionNames();

    public static ColumnCollectionsIdCollectionNames getInstance() {
        return instance;
    }

    private ColumnCollectionsIdCollectionNames() {
        super(
            TableCollections.getInstance(),
            "id_collectionnnames", // NOI18N
            DataType.Bigint);

        setIsPrimaryKey(true);
    }
}
