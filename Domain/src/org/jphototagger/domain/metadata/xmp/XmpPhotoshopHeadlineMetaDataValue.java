package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_headline</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopHeadlineMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopHeadlineMetaDataValue INSTANCE = new XmpPhotoshopHeadlineMetaDataValue();

    private XmpPhotoshopHeadlineMetaDataValue() {
        super("photoshop_headline", "xmp", ValueType.STRING);
        setValueLength(256);
        setDescription(Bundle.getString(XmpPhotoshopHeadlineMetaDataValue.class, "XmpPhotoshopHeadlineMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopHeadlineMetaDataValue.class, "XmpPhotoshopHeadlineMetaDataValue.LongerDescription"));
    }
}
