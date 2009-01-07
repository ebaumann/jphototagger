package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>iptc4xmpcore_location</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpIptc4xmpcoreLocation extends Column {

    private static final ColumnXmpIptc4xmpcoreLocation instance = new ColumnXmpIptc4xmpcoreLocation();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpIptc4xmpcoreLocation() {
        super(
            TableXmp.getInstance(),
            "iptc4xmpcore_location", // NOI18N
            DataType.String);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpIptc4xmpcoreLocation.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpIptc4xmpcoreLocation.LongerDescription"));
    }
}
