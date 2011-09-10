package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>dc_creators</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpDcCreatorMetaDataValue extends MetaDataValue {

    public static final XmpDcCreatorMetaDataValue INSTANCE = new XmpDcCreatorMetaDataValue();

    private XmpDcCreatorMetaDataValue() {
        super("creator", "dc_creators", ValueType.STRING);
        setValueLength(128);
        setDescription(Bundle.getString(XmpDcCreatorMetaDataValue.class, "XmpDcCreatorMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpDcCreatorMetaDataValue.class, "XmpDcCreatorMetaDataValue.LongerDescription"));
    }
}
