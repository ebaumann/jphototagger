package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_state</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopState extends Column {

    private static final ColumnXmpPhotoshopState instance = new ColumnXmpPhotoshopState();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopState() {
        super(
            TableXmp.getInstance(),
            "photoshop_state", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopState.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopState.LongerDescription"));
    }
}
