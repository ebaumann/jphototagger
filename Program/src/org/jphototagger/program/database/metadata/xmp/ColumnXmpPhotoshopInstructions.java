package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopInstructions extends Column {
    public static final ColumnXmpPhotoshopInstructions INSTANCE =
        new ColumnXmpPhotoshopInstructions();

    private ColumnXmpPhotoshopInstructions() {
        super("photoshop_instructions", "xmp", DataType.STRING);
        setLength(256);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopInstructions.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopInstructions.LongerDescription"));
    }
}
