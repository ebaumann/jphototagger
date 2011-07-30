package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

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
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopCountry.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopCountry.LongerDescription"));
    }
}
