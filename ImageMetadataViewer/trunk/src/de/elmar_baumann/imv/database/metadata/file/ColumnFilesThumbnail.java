package de.elmar_baumann.imv.database.metadata.file;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>thumbnail</code> der Tabelle <code>files</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public final class ColumnFilesThumbnail extends Column {

    public static final ColumnFilesThumbnail INSTANCE = new ColumnFilesThumbnail();

    private ColumnFilesThumbnail() {
        super(
            TableFiles.INSTANCE,
            "thumbnail", // NOI18N
            DataType.BINARY);

        setDescription(Bundle.getString("ColumnFilesThumbnail.Description")); // NOI18N
    }
}
