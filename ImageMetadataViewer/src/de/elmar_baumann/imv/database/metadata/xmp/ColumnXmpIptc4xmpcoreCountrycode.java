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

    private static final ColumnXmpIptc4xmpcoreCountrycode instance = new ColumnXmpIptc4xmpcoreCountrycode();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpIptc4xmpcoreCountrycode() {
        super(
            TableXmp.getInstance(),
            "iptc4xmpcore_countrycode", // NOI18N
            DataType.String);

        setLength(3);
        setDescription(Bundle.getString("ColumnXmpIptc4xmpcoreCountrycode.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpIptc4xmpcoreCountrycode.LongerDescription"));
    }
}
