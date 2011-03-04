package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpDcRights.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpDcRights.LongerDescription"));
    }
}
