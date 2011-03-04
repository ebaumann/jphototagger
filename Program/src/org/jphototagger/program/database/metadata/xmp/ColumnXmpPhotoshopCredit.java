package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_credits</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopCredit extends Column {
    public static final ColumnXmpPhotoshopCredit INSTANCE = new ColumnXmpPhotoshopCredit();

    private ColumnXmpPhotoshopCredit() {
        super("credit", "photoshop_credits", DataType.STRING);
        setLength(32);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCredit.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCredit.LongerDescription"));
    }
}
