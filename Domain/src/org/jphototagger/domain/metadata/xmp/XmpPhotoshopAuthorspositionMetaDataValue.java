package org.jphototagger.domain.metadata.xmp;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>photoshop_authorspositions</code> der Tabelle <code>xmp</code>.
 *
 * @author Elmar Baumann
 */
public final class XmpPhotoshopAuthorspositionMetaDataValue extends MetaDataValue {

    public static final XmpPhotoshopAuthorspositionMetaDataValue INSTANCE = new XmpPhotoshopAuthorspositionMetaDataValue();

    private XmpPhotoshopAuthorspositionMetaDataValue() {
        super("authorsposition", "photoshop_authorspositions", ValueType.STRING);
        setValueLength(32);
        setDescription(Bundle.getString(XmpPhotoshopAuthorspositionMetaDataValue.class, "XmpPhotoshopAuthorspositionMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpPhotoshopAuthorspositionMetaDataValue.class, "XmpPhotoshopAuthorspositionMetaDataValue.LongerDescription"));
    }
}
