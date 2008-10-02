package de.elmar_baumann.imagemetadataviewer.database.metadata.iptc;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>country_primary_location_name</code> der Tabelle <code>iptc</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class ColumnIptcCountryPrimaryLocationName extends Column {

    private static ColumnIptcCountryPrimaryLocationName instance = new ColumnIptcCountryPrimaryLocationName();

    public static Column getInstance() {
        return instance;
    }

    private ColumnIptcCountryPrimaryLocationName() {
        super(
            TableIptc.getInstance(),
            "country_primary_location_name", // NOI18N
            DataType.string);

        setLength(64);
        setDescription(Bundle.getString("ColumnIptcCountryPrimaryLocationName.Description"));
    }
}
