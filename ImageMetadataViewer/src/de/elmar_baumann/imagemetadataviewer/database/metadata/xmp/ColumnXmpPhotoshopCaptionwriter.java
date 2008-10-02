package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_captionwriter</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopCaptionwriter extends Column {

    private static ColumnXmpPhotoshopCaptionwriter instance = new ColumnXmpPhotoshopCaptionwriter();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopCaptionwriter() {
        super(
            TableXmp.getInstance(),
            "photoshop_captionwriter", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopCaptionwriter.Description"));
    }
}
