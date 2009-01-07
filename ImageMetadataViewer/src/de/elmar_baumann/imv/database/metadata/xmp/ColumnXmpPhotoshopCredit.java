package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_credit</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopCredit extends Column {

    private static final ColumnXmpPhotoshopCredit instance = new ColumnXmpPhotoshopCredit();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCredit() {
        super(
            TableXmp.getInstance(),
            "photoshop_credit", // NOI18N
            DataType.String);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCredit.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCredit.LongerDescription"));
    }
}
