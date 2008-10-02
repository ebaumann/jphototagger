package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_country</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopCountry extends Column {

    private static ColumnXmpPhotoshopCountry instance = new ColumnXmpPhotoshopCountry();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCountry() {
        super(
            TableXmp.getInstance(),
            "photoshop_country", // NOI18N
            DataType.string);

        setLength(64);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCountry.Description"));
    }
}
