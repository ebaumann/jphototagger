package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007-07-29
 */
public final class ColumnFilesLastModified extends Column {

    public static final ColumnFilesLastModified INSTANCE = new ColumnFilesLastModified();

    private ColumnFilesLastModified() {
        super(
            TableFiles.INSTANCE,
            "lastmodified", // NOI18N
            DataType.DATE);

        setDescription(Bundle.getString("ColumnFilesLastModified.Description")); // NOI18N
    }
}
