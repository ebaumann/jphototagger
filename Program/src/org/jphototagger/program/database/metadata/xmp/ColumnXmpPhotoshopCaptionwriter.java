package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Spalte <code>photoshop_captionwriters</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpPhotoshopCaptionwriter extends Column {
    public static final ColumnXmpPhotoshopCaptionwriter INSTANCE = new ColumnXmpPhotoshopCaptionwriter();

    private ColumnXmpPhotoshopCaptionwriter() {
        super("captionwriter", "photoshop_captionwriters", DataType.STRING);
        setLength(32);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCaptionwriter.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpPhotoshopCaptionwriter.LongerDescription"));
    }
}
