package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_countries</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopCountryMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopCountryMetaDataValue INSTANCE = new XmpPhotoshopCountryMetaDataValue();

    private XmpPhotoshopCountryMetaDataValue() {
        super("country", "photoshop_countries", ValueType.STRING);
        setValueLength(64);
        setDescription(Bundle.getString(XmpPhotoshopCountryMetaDataValue.class, "XmpPhotoshopCountryMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopCountryMetaDataValue.class, "XmpPhotoshopCountryMetaDataValue.LongerDescription"));
    }
}
