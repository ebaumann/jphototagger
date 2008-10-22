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
public class ColumnXmpPhotoshopSource extends Column {

    private static ColumnXmpPhotoshopSource instance = new ColumnXmpPhotoshopSource();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopSource() {
        super(
            TableXmp.getInstance(),
            "photoshop_source", // NOI18N
            DataType.String);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopSource.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopSource.LongerDescription"));
    }
}
