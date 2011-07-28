package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>dc_creators</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcCreator extends Column {

    public static final ColumnXmpDcCreator INSTANCE = new ColumnXmpDcCreator();

    private ColumnXmpDcCreator() {
        super("creator", "dc_creators", DataType.STRING);
        setLength(128);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpDcCreator.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpDcCreator.LongerDescription"));
    }
}
