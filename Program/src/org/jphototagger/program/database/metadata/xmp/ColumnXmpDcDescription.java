package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>dc_description</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcDescription extends Column {
    public static final ColumnXmpDcDescription INSTANCE =
        new ColumnXmpDcDescription();

    private ColumnXmpDcDescription() {
        super("dc_description", "xmp", DataType.STRING);
        setLength(2000);
        setDescription(
            JptBundle.INSTANCE.getString("ColumnXmpDcDescription.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpDcDescription.LongerDescription"));
    }
}
