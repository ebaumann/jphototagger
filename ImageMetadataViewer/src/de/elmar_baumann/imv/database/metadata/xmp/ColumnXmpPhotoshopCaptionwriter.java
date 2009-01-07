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

    private static final ColumnXmpPhotoshopCaptionwriter instance = new ColumnXmpPhotoshopCaptionwriter();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCaptionwriter() {
        super(
            TableXmp.getInstance(),
            "photoshop_captionwriter", // NOI18N
            DataType.String);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCaptionwriter.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopCaptionwriter.LongerDescription"));
    }
}
