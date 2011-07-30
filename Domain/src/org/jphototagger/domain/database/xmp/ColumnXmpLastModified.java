package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;

/**
 * Column <code>lastmodified</code> of table <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpLastModified extends Column {

    public static final ColumnXmpLastModified INSTANCE = new ColumnXmpLastModified();

    private ColumnXmpLastModified() {
        super("lastmodified", "xmp", DataType.BIGINT);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpLastModified.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpLastModified.LongerDescription"));
    }
}
