package org.jphototagger.domain.database.xmp;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.resource.Bundle;

/**
 * Column <code>lastmodified</code> of table <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpLastModified extends Column {

    public static final ColumnXmpLastModified INSTANCE = new ColumnXmpLastModified();

    private ColumnXmpLastModified() {
        super("lastmodified", "xmp", DataType.BIGINT);
        setDescription(Bundle.getString(ColumnXmpLastModified.class, "ColumnXmpLastModified.Description"));
        setLongerDescription(Bundle.getString(ColumnXmpLastModified.class, "ColumnXmpLastModified.LongerDescription"));
    }
}
