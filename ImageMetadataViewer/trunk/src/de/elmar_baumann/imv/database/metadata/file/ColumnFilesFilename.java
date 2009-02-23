package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>filename</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public final class ColumnFilesFilename extends Column {

    public static final ColumnFilesFilename INSTANCE = new ColumnFilesFilename();

    private ColumnFilesFilename() {
        super(
            TableFiles.INSTANCE,
            "filename", // NOI18N
            DataType.STRING);

        setLength(512);
        setDescription(Bundle.getString("ColumnFilesFilename.Description"));
    }
}
