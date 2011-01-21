package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_authorspositions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopAuthorsposition extends Column {
    public static final ColumnXmpPhotoshopAuthorsposition INSTANCE =
        new ColumnXmpPhotoshopAuthorsposition();

    private ColumnXmpPhotoshopAuthorsposition() {
        super("authorsposition", "photoshop_authorspositions", DataType.STRING);
        setLength(32);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopAuthorsposition.Description"));
        setLongerDescription(
            JptBundle.INSTANCE.getString(
                "ColumnXmpPhotoshopAuthorsposition.LongerDescription"));
    }
}
