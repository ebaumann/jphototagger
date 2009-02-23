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

    public static final ColumnCollectionnamesId INSTANCE = new ColumnCollectionnamesId();

    private ColumnCollectionnamesId() {
        super(
            TableCollectionNames.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
