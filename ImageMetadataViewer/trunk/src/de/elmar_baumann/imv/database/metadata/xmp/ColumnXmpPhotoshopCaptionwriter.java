package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_captionwriter</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopCaptionwriter extends Column {

    public static final ColumnXmpPhotoshopCaptionwriter INSTANCE = new ColumnXmpPhotoshopCaptionwriter();

    private ColumnXmpPhotoshopCaptionwriter() {
        super(
            TableXmp.INSTANCE,
            "photoshop_captionwriter", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCaptionwriter.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCaptionwriter.LongerDescription"));
    }
}
