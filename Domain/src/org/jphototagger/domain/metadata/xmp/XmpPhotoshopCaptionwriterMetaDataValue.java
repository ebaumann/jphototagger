package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_captionwriters</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopCaptionwriterMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopCaptionwriterMetaDataValue INSTANCE = new XmpPhotoshopCaptionwriterMetaDataValue();

    private XmpPhotoshopCaptionwriterMetaDataValue() {
        super("captionwriter", "photoshop_captionwriters", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopCaptionwriterMetaDataValue.class, "XmpPhotoshopCaptionwriterMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopCaptionwriterMetaDataValue.class, "XmpPhotoshopCaptionwriterMetaDataValue.LongerDescription"));
    }
}
