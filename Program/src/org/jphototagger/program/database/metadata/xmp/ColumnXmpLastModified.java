package org.jphototagger.program.database.metadata.xmp;

import org.jphototagger.domain.Column;
import org.jphototagger.program.resource.JptBundle;

/**
 * Column <code>lastmodified</code> of table <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnXmpLastModified extends Column {
    public static final ColumnXmpLastModified INSTANCE = new ColumnXmpLastModified();

    private ColumnXmpLastModified() {
        super("lastmodified", "xmp", DataType.BIGINT);
        setDescription(JptBundle.INSTANCE.getString("ColumnXmpLastModified.Description"));
        setLongerDescription(JptBundle.INSTANCE.getString("ColumnXmpLastModified.LongerDescription"));
    }
}
