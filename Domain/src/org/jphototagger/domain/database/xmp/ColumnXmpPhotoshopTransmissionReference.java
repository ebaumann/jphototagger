package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

/**
 * Spalte <code>photoshop_transmissionReference</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopTransmissionReference extends Column {

    public static final ColumnXmpPhotoshopTransmissionReference INSTANCE = new ColumnXmpPhotoshopTransmissionReference();

    private ColumnXmpPhotoshopTransmissionReference() {
        super("photoshop_transmissionReference", "xmp", DataType.STRING);
        setLength(32);
        setDescription(Bundle.getString(ColumnXmpPhotoshopTransmissionReference.class, "ColumnXmpPhotoshopTransmissionReference.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopTransmissionReference.class, "ColumnXmpPhotoshopTransmissionReference.LongerDescription"));
    }
}
