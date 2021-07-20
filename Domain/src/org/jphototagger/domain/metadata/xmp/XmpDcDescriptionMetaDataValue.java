package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>dc_description</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpDcDescriptionMetaDataValue extends MetaDataValue {

    public static final XmpDcDescriptionMetaDataValue INSTANCE = new XmpDcDescriptionMetaDataValue();

    private XmpDcDescriptionMetaDataValue() {
        super("dc_description", "xmp", ValueType.STRING);
        setValueLength(2000);
        setDescription(Bundle.getString(XmpDcDescriptionMetaDataValue.class, "XmpDcDescriptionMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpDcDescriptionMetaDataValue.class, "XmpDcDescriptionMetaDataValue.LongerDescription"));
    }
}
