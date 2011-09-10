package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>dc_rights</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpDcRightsMetaDataValue extends MetaDataValue {

    public static final XmpDcRightsMetaDataValue INSTANCE = new XmpDcRightsMetaDataValue();

    private XmpDcRightsMetaDataValue() {
        super("rights", "dc_rights", ValueType.STRING);
        setValueLength(128);
        setDescription(Bundle.getString(XmpDcRightsMetaDataValue.class, "XmpDcRightsMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpDcRightsMetaDataValue.class, "XmpDcRightsMetaDataValue.LongerDescription"));
    }
}
