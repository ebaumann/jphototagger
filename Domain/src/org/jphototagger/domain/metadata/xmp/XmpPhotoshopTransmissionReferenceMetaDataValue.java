package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_transmissionReference</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopTransmissionReferenceMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopTransmissionReferenceMetaDataValue INSTANCE = new XmpPhotoshopTransmissionReferenceMetaDataValue();

    private XmpPhotoshopTransmissionReferenceMetaDataValue() {
        super("photoshop_transmissionReference", "xmp", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopTransmissionReferenceMetaDataValue.class, "XmpPhotoshopTransmissionReferenceMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopTransmissionReferenceMetaDataValue.class, "XmpPhotoshopTransmissionReferenceMetaDataValue.LongerDescription"));
    }
}
