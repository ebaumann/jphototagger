package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_cities</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopCityMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopCityMetaDataValue INSTANCE = new XmpPhotoshopCityMetaDataValue();

    private XmpPhotoshopCityMetaDataValue() {
        super("city", "photoshop_cities", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopCityMetaDataValue.class, "XmpPhotoshopCityMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopCityMetaDataValue.class, "XmpPhotoshopCityMetaDataValue.LongerDescription"));
    }
}
