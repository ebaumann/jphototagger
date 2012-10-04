package org.jphototagger.domain.metadata.mapping;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;

/**
 * Returns, whether a XMP metadata value is repeatable.
 *
 * @author Elmar Baumann
 */
public final class XmpRepeatableValues {

    private static final Map<MetaDataValue, Boolean> IS_REPEATABLE = new HashMap<MetaDataValue, Boolean>();

    static {
        IS_REPEATABLE.put(XmpDcCreatorMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpDcDescriptionMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpDcRightsMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, true);
        IS_REPEATABLE.put(XmpDcTitleMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopCreditMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpLastModifiedMetaDataValue.INSTANCE, false);
        IS_REPEATABLE.put(XmpRatingMetaDataValue.INSTANCE, false);
    }

    public static boolean isRepeatable(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        Boolean repeatable = IS_REPEATABLE.get(value);

        if (repeatable == null) {
            throw new IllegalArgumentException("Unknown value: " + value);
        }

        return repeatable;
    }

    private XmpRepeatableValues() {
    }
}
