package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

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
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopTransmissionReference.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopTransmissionReference.LongerDescription"));
    }
}
