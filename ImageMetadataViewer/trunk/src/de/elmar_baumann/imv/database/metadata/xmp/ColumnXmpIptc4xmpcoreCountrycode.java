package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>iptc4xmpcore_countrycode</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpIptc4xmpcoreCountrycode extends Column {

    public static final ColumnXmpIptc4xmpcoreCountrycode INSTANCE = new ColumnXmpIptc4xmpcoreCountrycode();

    private ColumnXmpIptc4xmpcoreCountrycode() {
        super(
            TableXmp.INSTANCE,
            "iptc4xmpcore_countrycode", // NOI18N
            DataType.STRING);

        setLength(3);
        setDescription(Bundle.getString("ColumnXmpIptc4xmpcoreCountrycode.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpIptc4xmpcoreCountrycode.LongerDescription"));
    }
}
