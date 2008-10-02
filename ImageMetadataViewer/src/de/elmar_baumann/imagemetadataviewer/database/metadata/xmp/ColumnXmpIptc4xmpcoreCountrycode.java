package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>iptc4xmpcore_countrycode</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpIptc4xmpcoreCountrycode extends Column {

    private static ColumnXmpIptc4xmpcoreCountrycode instance = new ColumnXmpIptc4xmpcoreCountrycode();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpIptc4xmpcoreCountrycode() {
        super(
            TableXmp.getInstance(),
            "iptc4xmpcore_countrycode", // NOI18N
            DataType.string);

        setLength(3);
        setDescription(Bundle.getString("ColumnXmpIptc4xmpcoreCountrycode.Description"));
    }
}
