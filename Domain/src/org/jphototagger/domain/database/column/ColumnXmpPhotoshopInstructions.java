package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopInstructions extends Column {

    public static final ColumnXmpPhotoshopInstructions INSTANCE = new ColumnXmpPhotoshopInstructions();

    private ColumnXmpPhotoshopInstructions() {
        super("photoshop_instructions", "xmp", DataType.STRING);
        setLength(256);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopInstructions.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopInstructions.LongerDescription"));
    }
}
