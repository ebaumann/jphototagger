package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>dc_title</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcTitle extends Column {

    public static final ColumnXmpDcTitle INSTANCE = new ColumnXmpDcTitle();

    private ColumnXmpDcTitle() {
        super("dc_title", "xmp", DataType.STRING);
        setLength(64);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpDcTitle.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpDcTitle.LongerDescription"));
    }
}
