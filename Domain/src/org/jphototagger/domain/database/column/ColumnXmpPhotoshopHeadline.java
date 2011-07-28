package org.jphototagger.domain.database.column;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;

/**
 * Spalte <code>photoshop_headline</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopHeadline extends Column {

    public static final ColumnXmpPhotoshopHeadline INSTANCE = new ColumnXmpPhotoshopHeadline();

    private ColumnXmpPhotoshopHeadline() {
        super("photoshop_headline", "xmp", DataType.STRING);
        setLength(256);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopHeadline.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpPhotoshopHeadline.LongerDescription"));
    }
}
