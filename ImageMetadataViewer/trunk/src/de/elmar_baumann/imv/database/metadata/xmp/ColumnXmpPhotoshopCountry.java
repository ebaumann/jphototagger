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

    public static final ColumnXmpPhotoshopCountry INSTANCE = new ColumnXmpPhotoshopCountry();

    private ColumnXmpPhotoshopCountry() {
        super(
            TableXmp.INSTANCE,
            "photoshop_country", // NOI18N
            DataType.STRING);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCountry.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCountry.LongerDescription")); // NOI18N
    }
}
