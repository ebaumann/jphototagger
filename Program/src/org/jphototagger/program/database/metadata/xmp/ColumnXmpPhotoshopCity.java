package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_cities</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopCity extends Column {
    public static final ColumnXmpPhotoshopCity INSTANCE = new ColumnXmpPhotoshopCity();

    private ColumnXmpPhotoshopCity() {
        super("city", "photoshop_cities", DataType.STRING);
        setLength(32);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCity.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCity.LongerDescription"));
    }
}
