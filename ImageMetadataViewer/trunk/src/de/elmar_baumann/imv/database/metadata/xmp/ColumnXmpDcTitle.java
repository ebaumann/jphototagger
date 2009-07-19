package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>dc_title</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpDcTitle extends Column {

    public static final ColumnXmpDcTitle INSTANCE = new ColumnXmpDcTitle();

    private ColumnXmpDcTitle() {
        super(
            TableXmp.INSTANCE,
            "dc_title", // NOI18N
            DataType.STRING);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpDcTitle.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpDcTitle.LongerDescription")); // NOI18N
    }
}
