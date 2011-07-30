package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

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
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopCredit.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopCredit.LongerDescription"));
    }
}
