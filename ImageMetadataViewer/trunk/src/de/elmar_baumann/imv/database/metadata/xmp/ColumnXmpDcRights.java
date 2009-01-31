package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>dc_rights</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpDcRights extends Column {

    private static final ColumnXmpDcRights instance = new ColumnXmpDcRights();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcRights() {
        super(
            TableXmp.getInstance(),
            "dc_rights", // NOI18N
            DataType.STRING);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcRights.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpDcRights.LongerDescription"));
    }
}
