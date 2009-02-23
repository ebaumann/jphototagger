package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_authorsposition</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopAuthorsposition extends Column {

    public static final ColumnXmpPhotoshopAuthorsposition INSTANCE = new ColumnXmpPhotoshopAuthorsposition();

    private ColumnXmpPhotoshopAuthorsposition() {
        super(
            TableXmp.INSTANCE,
            "photoshop_authorsposition", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopAuthorsposition.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopAuthorsposition.LongerDescription"));
    }
}
