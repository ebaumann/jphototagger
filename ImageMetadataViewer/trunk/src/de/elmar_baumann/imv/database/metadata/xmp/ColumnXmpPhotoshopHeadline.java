package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_headline</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class ColumnXmpPhotoshopHeadline extends Column {

    public static final ColumnXmpPhotoshopHeadline INSTANCE = new ColumnXmpPhotoshopHeadline();

    private ColumnXmpPhotoshopHeadline() {
        super(
            TableXmp.INSTANCE,
            "photoshop_headline", // NOI18N
            DataType.STRING);

        setLength(256);
        setDescription(Bundle.getString("ColumnXmpPhotoshopHeadline.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopHeadline.LongerDescription")); // NOI18N
    }
}
