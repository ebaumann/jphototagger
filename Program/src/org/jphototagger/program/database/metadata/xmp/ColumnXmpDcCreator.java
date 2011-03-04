package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpDcCreator.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpDcCreator.LongerDescription"));
    }
}
