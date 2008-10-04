package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_headline</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopHeadline extends Column {

    private static ColumnXmpPhotoshopHeadline instance = new ColumnXmpPhotoshopHeadline();

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
    }
}
