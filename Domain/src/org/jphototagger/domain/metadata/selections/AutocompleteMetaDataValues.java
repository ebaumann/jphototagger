package org.jphototagger.domain.metadata.selections;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;

/**
 * All metadata values with autocomplete enabled, for other values autocomplete should be disabled.
 *
 * @author Elmar Baumann
 */
public final class AutocompleteMetaDataValues {

    private static final List<MetaDataValue> VALUES = new ArrayList<>();

    static {
        VALUES.add(XmpDcCreatorMetaDataValue.INSTANCE);
        VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        VALUES.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
    }

    private AutocompleteMetaDataValues() {
    }

    public static boolean contains(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return VALUES.contains(value);
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<>(VALUES);
    }
}
