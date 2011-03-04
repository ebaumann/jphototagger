package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_sources</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopSource extends Column {
    public static final ColumnXmpPhotoshopSource INSTANCE = new ColumnXmpPhotoshopSource();

    private ColumnXmpPhotoshopSource() {
        super("source", "photoshop_sources", DataType.STRING);
        setLength(32);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopSource.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopSource.LongerDescription"));
    }
}
