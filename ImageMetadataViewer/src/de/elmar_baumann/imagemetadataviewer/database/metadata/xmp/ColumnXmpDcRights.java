package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>dc_rights</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpDcRights extends Column {

    private static ColumnXmpDcRights instance = new ColumnXmpDcRights();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcRights() {
        super(
            TableXmp.getInstance(),
            "dc_rights", // NOI18N
            DataType.String);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcRights.Description"));
    }
}
