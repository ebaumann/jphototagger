package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>iptc4xmpcore_location</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpIptc4xmpcoreLocation extends Column {

    public static final ColumnXmpIptc4xmpcoreLocation INSTANCE = new ColumnXmpIptc4xmpcoreLocation();

    private ColumnXmpIptc4xmpcoreLocation() {
        super(
            TableXmp.INSTANCE,
            "iptc4xmpcore_location", // NOI18N
            DataType.STRING);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpIptc4xmpcoreLocation.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpIptc4xmpcoreLocation.LongerDescription")); // NOI18N
    }
}
