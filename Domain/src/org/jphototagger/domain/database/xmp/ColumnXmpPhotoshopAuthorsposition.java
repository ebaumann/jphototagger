package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_authorspositions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopAuthorsposition extends Column {

    public static final ColumnXmpPhotoshopAuthorsposition INSTANCE = new ColumnXmpPhotoshopAuthorsposition();

    private ColumnXmpPhotoshopAuthorsposition() {
        super("authorsposition", "photoshop_authorspositions", DataType.STRING);
        setLength(32);
        setDescription(Bundle.getString(ColumnXmpPhotoshopAuthorsposition.class, "ColumnXmpPhotoshopAuthorsposition.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopAuthorsposition.class, "ColumnXmpPhotoshopAuthorsposition.LongerDescription"));
    }
}
