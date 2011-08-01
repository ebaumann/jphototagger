package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_states</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopState extends Column {

    public static final ColumnXmpPhotoshopState INSTANCE = new ColumnXmpPhotoshopState();

    private ColumnXmpPhotoshopState() {
        super("state", "photoshop_states", DataType.STRING);
        setLength(32);
        setDescription(Bundle.getString(ColumnXmpPhotoshopState.class, "ColumnXmpPhotoshopState.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopState.class, "ColumnXmpPhotoshopState.LongerDescription"));
    }
}
