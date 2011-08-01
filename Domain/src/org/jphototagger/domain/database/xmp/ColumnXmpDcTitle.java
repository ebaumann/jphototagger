package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

/**
 * Spalte <code>dc_title</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpDcTitle extends Column {

    public static final ColumnXmpDcTitle INSTANCE = new ColumnXmpDcTitle();

    private ColumnXmpDcTitle() {
        super("dc_title", "xmp", DataType.STRING);
        setLength(64);
        setDescription(Bundle.getString(ColumnXmpDcTitle.class, "ColumnXmpDcTitle.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpDcTitle.class, "ColumnXmpDcTitle.LongerDescription"));
    }
}
