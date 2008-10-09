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
public class ColumnXmpDcDescription extends Column {

    private static ColumnXmpDcDescription instance = new ColumnXmpDcDescription();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcDescription() {
        super(
            TableXmp.getInstance(),
            "dc_description", // NOI18N
            DataType.String);

        setLength(2000);
        setDescription(Bundle.getString("ColumnXmpDcDescription.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpDcDescription.LongerDescription"));
    }
}
