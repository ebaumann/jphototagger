package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>dc_creators</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcCreator extends Column {

    public static final ColumnXmpDcCreator INSTANCE = new ColumnXmpDcCreator();

    private ColumnXmpDcCreator() {
        super("creator", "dc_creators", DataType.STRING);
        setLength(128);
        setDescription(Bundle.getString(ColumnXmpDcCreator.class, "ColumnXmpDcCreator.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpDcCreator.class, "ColumnXmpDcCreator.LongerDescription"));
    }
}
