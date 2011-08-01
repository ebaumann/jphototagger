package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>iptc4xmpcore_locations</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpIptc4xmpcoreLocation extends Column {

    public static final ColumnXmpIptc4xmpcoreLocation INSTANCE = new ColumnXmpIptc4xmpcoreLocation();

    private ColumnXmpIptc4xmpcoreLocation() {
        super("location", "iptc4xmpcore_locations", DataType.STRING);
        setLength(64);
        setDescription(Bundle.getString(ColumnXmpIptc4xmpcoreLocation.class, "ColumnXmpIptc4xmpcoreLocation.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpIptc4xmpcoreLocation.class, "ColumnXmpIptc4xmpcoreLocation.LongerDescription"));
    }
}
