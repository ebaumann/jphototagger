package de.elmar_baumann.imagemetadataviewer.database.metadata.file;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;

/**
 * Spalte <code>id</code> der Tabelle <code>files</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnFilesId extends Column {

    private static ColumnFilesId instance = new ColumnFilesId();

    public static ColumnFilesId getInstance() {
        return instance;
    }

    private ColumnFilesId() {
        super(
            TableFiles.getInstance(),
            "id", // NOI18N
            DataType.integer);

        setIsPrimaryKey(true);
    }
}
