package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>dc_title</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpDcTitle extends Column {

    private static ColumnXmpDcTitle instance = new ColumnXmpDcTitle();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcTitle() {
        super(
            TableXmp.getInstance(),
            "dc_title", // NOI18N
            DataType.String);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpDcTitle.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpDcTitle.LongerDescription"));
    }
}
