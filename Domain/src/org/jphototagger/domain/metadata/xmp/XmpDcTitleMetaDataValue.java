package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>dc_title</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpDcTitleMetaDataValue extends MetaDataValue {

    public static final XmpDcTitleMetaDataValue INSTANCE = new XmpDcTitleMetaDataValue();

    private XmpDcTitleMetaDataValue() {
        super("dc_title", "xmp", ValueType.STRING);
        setValueLength(64);
        setDescription(Bundle.getString(XmpDcTitleMetaDataValue.class, "XmpDcTitleMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpDcTitleMetaDataValue.class, "XmpDcTitleMetaDataValue.LongerDescription"));
    }
}
