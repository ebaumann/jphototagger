package de.elmar_baumann.imv.database.metadata.collections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;

/**
 * Spalte <code>id_files</code> der Tabelle <code>collections</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnCollectionsIdFiles extends Column {

    private static ColumnCollectionsIdFiles instance = new ColumnCollectionsIdFiles();

    public static ColumnCollectionsIdFiles getInstance() {
        return instance;
    }

    private ColumnCollectionsIdFiles() {
        super(
            TableCollections.getInstance(),
            "id_files", // NOI18N
            DataType.Bigint);

        setIsPrimaryKey(true);
    }
}
