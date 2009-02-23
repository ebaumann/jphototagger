package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_city</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopCity extends Column {

    public static ColumnXmpPhotoshopCity INSTANCE = new ColumnXmpPhotoshopCity();

    private ColumnXmpPhotoshopCity() {
        super(
            TableXmp.INSTANCE,
            "photoshop_city", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCity.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCity.LongerDescription"));
    }
}
