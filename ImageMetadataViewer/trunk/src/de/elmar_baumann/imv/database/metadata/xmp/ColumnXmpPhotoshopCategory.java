package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_category</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopCategory extends Column {

    public static final ColumnXmpPhotoshopCategory INSTANCE = new ColumnXmpPhotoshopCategory();

    private ColumnXmpPhotoshopCategory() {
        super(
            TableXmp.INSTANCE,
            "photoshop_category", // NOI18N
            DataType.STRING);

        setLength(128);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCategory.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCategory.LongerDescription"));
    }
}
