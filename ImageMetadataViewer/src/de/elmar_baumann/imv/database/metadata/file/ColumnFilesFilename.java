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
public class ColumnFilesFilename extends Column {

    private static ColumnFilesFilename instance = new ColumnFilesFilename();

    public static Column getInstance() {
        return instance;
    }

    private ColumnFilesFilename() {
        super(
            TableFiles.getInstance(),
            "filename", // NOI18N
            DataType.String);

        setLength(512);
        setDescription(Bundle.getString("ColumnFilesFilename.Description"));
    }
}
