package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>dc_creator</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpDcCreator extends Column {

    public static final ColumnXmpDcCreator INSTANCE = new ColumnXmpDcCreator();

    private ColumnXmpDcCreator() {
        super(
            TableXmp.INSTANCE,
            "dc_creator", // NOI18N
            DataType.STRING);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcCreator.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpDcCreator.LongerDescription")); // NOI18N
    }
}
