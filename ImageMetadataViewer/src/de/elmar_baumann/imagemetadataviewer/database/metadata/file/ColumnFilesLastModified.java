package de.elmar_baumann.imagemetadataviewer.database.metadata.file;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnFilesLastModified extends Column {

    private static ColumnFilesLastModified instance = new ColumnFilesLastModified();

    public static Column getInstance() {
        return instance;
    }

    private ColumnFilesLastModified() {
        super(
            TableFiles.getInstance(),
            "lastmodified", // NOI18N
            DataType.date);

        setDescription(Bundle.getString("ColumnFilesLastModified.Description"));
    }
}
