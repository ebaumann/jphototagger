package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

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
        setDescription(Bundle.getString(ColumnXmpPhotoshopInstructions.class, "ColumnXmpPhotoshopInstructions.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopInstructions.class, "ColumnXmpPhotoshopInstructions.LongerDescription"));
    }
}
