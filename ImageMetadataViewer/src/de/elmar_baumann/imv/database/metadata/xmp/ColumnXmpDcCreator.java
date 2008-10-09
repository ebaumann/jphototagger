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
public class ColumnXmpDcCreator extends Column {

    private static ColumnXmpDcCreator instance = new ColumnXmpDcCreator();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpDcCreator() {
        super(
            TableXmp.getInstance(),
            "dc_creator", // NOI18N
            DataType.String);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpDcCreatorsCreator.Description"));
    }
}
