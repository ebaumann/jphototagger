package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;

/**
 * Spalte <code>id</code> der Tabelle <code>collection_names</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public final class ColumnCollectionnamesId extends Column {

    private static final ColumnCollectionnamesId instance = new ColumnCollectionnamesId();

    public static ColumnCollectionnamesId getInstance() {
        return instance;
    }

    private ColumnCollectionnamesId() {
        super(
            TableCollectionNames.getInstance(),
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
