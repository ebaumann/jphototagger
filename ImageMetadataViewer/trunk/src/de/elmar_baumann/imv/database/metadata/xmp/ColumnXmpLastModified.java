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
    
    private static final ColumnXmpLastModified instance = new ColumnXmpLastModified();
    
    public static ColumnXmpLastModified getInstance() {
        return instance;
    }
    
    private ColumnXmpLastModified() {
        super(
            TableXmp.getInstance(),
            "lastmodified", // NOI18N
            DataType.Bigint);

        setDescription(Bundle.getString("ColumnXmpLastModified.Description"));
        setLongerDescription(Bundle.getString("ColumnXmpLastModified.LongerDescription"));
    }

}
