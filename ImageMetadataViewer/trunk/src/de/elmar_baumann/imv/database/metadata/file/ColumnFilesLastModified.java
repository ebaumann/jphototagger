package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public final class ColumnFilesLastModified extends Column {

    private static final ColumnFilesLastModified instance = new ColumnFilesLastModified();

    public static Column getInstance() {
        return instance;
    }

    private ColumnFilesLastModified() {
        super(
            TableFiles.getInstance(),
            "lastmodified", // NOI18N
            DataType.DATE);

        setDescription(Bundle.getString("ColumnFilesLastModified.Description"));
    }
}
