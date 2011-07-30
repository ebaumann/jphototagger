package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>dc_rights</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcRights extends Column {

    public static final ColumnXmpDcRights INSTANCE = new ColumnXmpDcRights();

    private ColumnXmpDcRights() {
        super("rights", "dc_rights", DataType.STRING);
        setLength(128);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpDcRights.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpDcRights.LongerDescription"));
    }
}
