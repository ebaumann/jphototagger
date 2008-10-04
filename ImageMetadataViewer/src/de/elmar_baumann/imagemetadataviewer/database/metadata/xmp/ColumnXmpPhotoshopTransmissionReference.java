package de.elmar_baumann.imagemetadataviewer.database.metadata.xmp;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column.DataType;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Spalte <code>photoshop_transmissionReference</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class ColumnXmpPhotoshopTransmissionReference extends Column {

    private static ColumnXmpPhotoshopTransmissionReference instance = new ColumnXmpPhotoshopTransmissionReference();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopTransmissionReference() {
        super(
            TableXmp.getInstance(),
            "photoshop_transmissionReference", // NOI18N
            DataType.String);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopTransmissionReference.Description"));
    }
}
