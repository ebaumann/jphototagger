package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_credit</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpPhotoshopCredit extends Column {

    public static final ColumnXmpPhotoshopCredit INSTANCE = new ColumnXmpPhotoshopCredit();

    private ColumnXmpPhotoshopCredit() {
        super(
            TableXmp.INSTANCE,
            "photoshop_credit", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCredit.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCredit.LongerDescription")); // NOI18N
    }
}
