package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_state</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopState extends Column {

    private static ColumnXmpPhotoshopState instance = new ColumnXmpPhotoshopState();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopState() {
        super(
            TableXmp.getInstance(),
            "photoshop_state", // NOI18N
            DataType.string);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopState.Description"));
    }
}
