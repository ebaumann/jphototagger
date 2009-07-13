package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_source</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopSource extends Column {

    public static final ColumnXmpPhotoshopSource INSTANCE = new ColumnXmpPhotoshopSource();

    private ColumnXmpPhotoshopSource() {
        super(
            TableXmp.INSTANCE,
            "photoshop_source", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopSource.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopSource.LongerDescription")); // NOI18N
    }
}
