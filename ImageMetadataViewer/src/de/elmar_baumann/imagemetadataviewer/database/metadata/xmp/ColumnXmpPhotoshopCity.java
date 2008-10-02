package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_city</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopCity extends Column {

    private static ColumnXmpPhotoshopCity instance = new ColumnXmpPhotoshopCity();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCity() {
        super(
            TableXmp.getInstance(),
            "photoshop_city", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCity.Description"));
    }
}
