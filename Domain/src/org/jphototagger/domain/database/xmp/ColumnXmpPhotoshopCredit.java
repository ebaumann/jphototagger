package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

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
        setDescription(Bundle.getString(ColumnXmpPhotoshopCredit.class, "ColumnXmpPhotoshopCredit.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopCredit.class, "ColumnXmpPhotoshopCredit.LongerDescription"));
    }
}
