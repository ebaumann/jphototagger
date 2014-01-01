package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_sources</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopSourceMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopSourceMetaDataValue INSTANCE = new XmpPhotoshopSourceMetaDataValue();

    private XmpPhotoshopSourceMetaDataValue() {
        super("source", "photoshop_sources", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopSourceMetaDataValue.class, "XmpPhotoshopSourceMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopSourceMetaDataValue.class, "XmpPhotoshopSourceMetaDataValue.LongerDescription"));
    }
}
