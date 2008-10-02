package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>province_state</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcProvinceState extends Column {

    private static ColumnIptcProvinceState instance = new ColumnIptcProvinceState();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcProvinceState() {
        super(
            TableIptc.getInstance(),
            "province_state", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnIptcProvinceState.Description"));
    }
}
