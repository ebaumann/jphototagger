package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_instructions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopInstructionsMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopInstructionsMetaDataValue INSTANCE = new XmpPhotoshopInstructionsMetaDataValue();

    private XmpPhotoshopInstructionsMetaDataValue() {
        super("photoshop_instructions", "xmp", ValueType.STRING);
        setValueLength(256);
        setDescription(Bundle.getString(XmpPhotoshopInstructionsMetaDataValue.class, "XmpPhotoshopInstructionsMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopInstructionsMetaDataValue.class, "XmpPhotoshopInstructionsMetaDataValue.LongerDescription"));
    }
}
