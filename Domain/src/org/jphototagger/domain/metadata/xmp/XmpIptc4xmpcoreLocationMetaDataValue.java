package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>iptc4xmpcore_locations</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpIptc4xmpcoreLocationMetaDataValue extends MetaDataValue {

    public static final XmpIptc4xmpcoreLocationMetaDataValue INSTANCE = new XmpIptc4xmpcoreLocationMetaDataValue();

    private XmpIptc4xmpcoreLocationMetaDataValue() {
        super("location", "iptc4xmpcore_locations", ValueType.STRING);
        setValueLength(64);
        setDescription(Bundle.getString(XmpIptc4xmpcoreLocationMetaDataValue.class, "XmpIptc4xmpcoreLocationMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpIptc4xmpcoreLocationMetaDataValue.class, "XmpIptc4xmpcoreLocationMetaDataValue.LongerDescription"));
    }
}
