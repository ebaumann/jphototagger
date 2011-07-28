package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

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
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopSource.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopSource.LongerDescription"));
    }
}
