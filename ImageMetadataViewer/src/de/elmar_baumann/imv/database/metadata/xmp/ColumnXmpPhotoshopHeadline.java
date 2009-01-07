package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_headline</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopHeadline extends Column {

    private static final ColumnXmpPhotoshopHeadline instance = new ColumnXmpPhotoshopHeadline();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopHeadline() {
        super(
            TableXmp.getInstance(),
            "photoshop_headline", // NOI18N
            DataType.String);

        setLength(256);
        setDescription(Bundle.getString("ColumnXmpPhotoshopHeadline.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopHeadline.LongerDescription"));
    }
}
