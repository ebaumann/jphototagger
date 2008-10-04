package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_category</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopCategory extends Column {

    private static ColumnXmpPhotoshopCategory instance = new ColumnXmpPhotoshopCategory();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCategory() {
        super(
            TableXmp.getInstance(),
            "photoshop_category", // NOI18N
            DataType.String);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCategory.Description"));
    }
}
