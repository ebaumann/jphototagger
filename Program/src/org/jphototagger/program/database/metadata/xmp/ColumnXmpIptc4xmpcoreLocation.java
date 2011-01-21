package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>iptc4xmpcore_locations</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpIptc4xmpcoreLocation extends Column {
    public static final ColumnXmpIptc4xmpcoreLocation INSTANCE =
        new ColumnXmpIptc4xmpcoreLocation();

    private ColumnXmpIptc4xmpcoreLocation() {
        super("location", "iptc4xmpcore_locations", DataType.STRING);
        setLength(64);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpIptc4xmpcoreLocation.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpIptc4xmpcoreLocation.LongerDescription"));
    }
}
