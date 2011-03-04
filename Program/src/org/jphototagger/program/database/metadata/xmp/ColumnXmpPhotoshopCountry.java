package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_countries</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopCountry extends Column {
    public static final ColumnXmpPhotoshopCountry INSTANCE = new ColumnXmpPhotoshopCountry();

    private ColumnXmpPhotoshopCountry() {
        super("country", "photoshop_countries", DataType.STRING);
        setLength(64);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCountry.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCountry.LongerDescription"));
    }
}
