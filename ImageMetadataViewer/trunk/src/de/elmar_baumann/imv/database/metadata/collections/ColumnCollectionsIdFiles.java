package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;

/**
 * Spalte <code>id_files</code> der Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnCollectionsIdFiles extends Column {

    public static final ColumnCollectionsIdFiles INSTANCE = new ColumnCollectionsIdFiles();

    private ColumnCollectionsIdFiles() {
        super(
            TableCollections.INSTANCE,
            "id_files", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
