package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_states</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopState extends Column {
    public static final ColumnXmpPhotoshopState INSTANCE =
        new ColumnXmpPhotoshopState();

    private ColumnXmpPhotoshopState() {
        super("state", "photoshop_states", DataType.STRING);
        setLength(32);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopState.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopState.LongerDescription"));
    }
}
