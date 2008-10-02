package de.elmar_baumann.imagemetadataviewer.database.metadata.file;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Tabellenspalte <code>thumbnail</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnFilesThumbnail extends Column {

    private static ColumnFilesThumbnail instance = new ColumnFilesThumbnail();

    public static Column getInstance() {
        return instance;
    }

    private ColumnFilesThumbnail() {
        super(
            TableFiles.getInstance(),
            "thumbnail", // NOI18N
            DataType.binary);

        setDescription(Bundle.getString("ColumnFilesThumbnail.Description"));
    }
}
