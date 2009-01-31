package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>photoshop_transmissionReference</code> der Tabelle <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class ColumnXmpPhotoshopTransmissionReference extends Column {

    private static final ColumnXmpPhotoshopTransmissionReference instance = new ColumnXmpPhotoshopTransmissionReference();

    public static Column getInstance() {
        return instance;
    }

    private ColumnXmpPhotoshopTransmissionReference() {
        super(
            TableXmp.getInstance(),
            "photoshop_transmissionReference", // NOI18N
            DataType.STRING);

        setLength(32);
        setDescription(Bundle.getString("ColumnXmpPhotoshopTransmissionReference.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpPhotoshopTransmissionReference.LongerDescription"));
    }
}
