package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>dc_description</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpDcDescription extends Column {

    public static final ColumnXmpDcDescription INSTANCE = new ColumnXmpDcDescription();

    private ColumnXmpDcDescription() {
        super(
            TableXmp.INSTANCE,
            "dc_description", // NOI18N
            DataType.STRING);

        setLength(2000);
        setDescription(Bundle.getString("ColumnXmpDcDescription.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpDcDescription.LongerDescription")); // NOI18N
    }
}
