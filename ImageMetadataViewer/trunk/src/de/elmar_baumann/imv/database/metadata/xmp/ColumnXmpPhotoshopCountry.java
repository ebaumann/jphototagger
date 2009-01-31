package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_country</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopCountry extends Column {

    private static final ColumnXmpPhotoshopCountry instance = new ColumnXmpPhotoshopCountry();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCountry() {
        super(
            TableXmp.getInstance(),
            "photoshop_country", // NOI18N
            DataType.STRING);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCountry.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCountry.LongerDescription"));
    }
}
