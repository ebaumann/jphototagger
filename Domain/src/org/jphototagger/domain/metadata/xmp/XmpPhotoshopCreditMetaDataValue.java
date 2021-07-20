package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_credits</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopCreditMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopCreditMetaDataValue INSTANCE = new XmpPhotoshopCreditMetaDataValue();

    private XmpPhotoshopCreditMetaDataValue() {
        super("credit", "photoshop_credits", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopCreditMetaDataValue.class, "XmpPhotoshopCreditMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopCreditMetaDataValue.class, "XmpPhotoshopCreditMetaDataValue.LongerDescription"));
    }
}
