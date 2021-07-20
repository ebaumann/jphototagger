package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_states</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopStateMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopStateMetaDataValue INSTANCE = new XmpPhotoshopStateMetaDataValue();

    private XmpPhotoshopStateMetaDataValue() {
        super("state", "photoshop_states", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopStateMetaDataValue.class, "XmpPhotoshopStateMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopStateMetaDataValue.class, "XmpPhotoshopStateMetaDataValue.LongerDescription"));
    }
}
