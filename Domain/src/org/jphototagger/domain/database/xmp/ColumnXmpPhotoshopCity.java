package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

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
        setDescription(Bundle.getString(ColumnXmpPhotoshopCity.class, "ColumnXmpPhotoshopCity.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopCity.class, "ColumnXmpPhotoshopCity.LongerDescription"));
    }
}
