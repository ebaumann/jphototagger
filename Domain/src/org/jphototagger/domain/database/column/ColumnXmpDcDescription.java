package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>dc_description</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcDescription extends Column {

    public static final ColumnXmpDcDescription INSTANCE = new ColumnXmpDcDescription();

    private ColumnXmpDcDescription() {
        super("dc_description", "xmp", DataType.STRING);
        setLength(2000);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpDcDescription.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpDcDescription.LongerDescription"));
    }
}
