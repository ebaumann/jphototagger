package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

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
        setDescription(Bundle.getString(ColumnXmpPhotoshopHeadline.class, "ColumnXmpPhotoshopHeadline.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopHeadline.class, "ColumnXmpPhotoshopHeadline.LongerDescription"));
    }
}
