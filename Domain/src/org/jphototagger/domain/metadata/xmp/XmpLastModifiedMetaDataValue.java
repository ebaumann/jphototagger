package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * MetaDataValue <code>lastmodified</code> of table <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpLastModifiedMetaDataValue extends MetaDataValue {

    public static final XmpLastModifiedMetaDataValue INSTANCE = new XmpLastModifiedMetaDataValue();

    private XmpLastModifiedMetaDataValue() {
        super("lastmodified", "xmp", ValueType.BIGINT);
        setDescription(Bundle.getString(XmpLastModifiedMetaDataValue.class, "XmpLastModifiedMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpLastModifiedMetaDataValue.class, "XmpLastModifiedMetaDataValue.LongerDescription"));
    }
}
