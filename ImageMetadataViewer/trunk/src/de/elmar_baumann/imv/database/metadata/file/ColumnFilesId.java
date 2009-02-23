package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>files</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnFilesId extends Column {

    public static final ColumnFilesId INSTANCE = new ColumnFilesId();

    private ColumnFilesId() {
        super(
            TableFiles.INSTANCE,
            "id", // NOI18N
            DataType.BIGINT);

        setIsPrimaryKey(true);
    }
}
