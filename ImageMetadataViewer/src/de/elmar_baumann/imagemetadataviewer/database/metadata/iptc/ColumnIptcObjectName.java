package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>object_name</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcObjectName extends Column {

    private static ColumnIptcObjectName instance = new ColumnIptcObjectName();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcObjectName() {
        super(
            TableIptc.getInstance(),
            "object_name", // NOI18N
            DataType.string);

        setLength(64);
        setDescription(Bundle.getString("ColumnIptcObjectName.Description"));
    }
}
