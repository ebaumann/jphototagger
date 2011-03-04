package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopHeadline.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopHeadline.LongerDescription"));
    }
}
