package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

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
        setDescription(Bundle.getString(ColumnXmpPhotoshopCaptionwriter.class, "ColumnXmpPhotoshopCaptionwriter.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpPhotoshopCaptionwriter.class, "ColumnXmpPhotoshopCaptionwriter.LongerDescription"));
    }
}
