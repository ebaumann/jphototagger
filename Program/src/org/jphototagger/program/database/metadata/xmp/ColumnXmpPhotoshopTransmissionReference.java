package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_transmissionReference</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopTransmissionReference extends Column {
    public static final ColumnXmpPhotoshopTransmissionReference INSTANCE =
        new ColumnXmpPhotoshopTransmissionReference();

    private ColumnXmpPhotoshopTransmissionReference() {
        super("photoshop_transmissionReference", "xmp", DataType.STRING);
        setLength(32);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopTransmissionReference.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopTransmissionReference.LongerDescription"));
    }
}
