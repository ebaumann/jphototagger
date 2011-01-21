package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(
            JptBundle.INSTANCE.getString("ColumnXmpDcTitle.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString("ColumnXmpDcTitle.LongerDescription"));
    }
}
