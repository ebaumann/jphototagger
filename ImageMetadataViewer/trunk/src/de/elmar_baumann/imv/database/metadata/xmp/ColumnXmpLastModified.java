package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Column <code>lastmodified</code> of table <code>xmp</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/23
 */
public final class ColumnXmpLastModified extends Column {
    
    public static final ColumnXmpLastModified INSTANCE = new ColumnXmpLastModified();
    
    private ColumnXmpLastModified() {
        super(
            TableXmp.INSTANCE,
            "lastmodified", // NOI18N
            DataType.BIGINT);

        setDescription(Bundle.getString("ColumnXmpLastModified.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpLastModified.LongerDescription"));
    }

}
