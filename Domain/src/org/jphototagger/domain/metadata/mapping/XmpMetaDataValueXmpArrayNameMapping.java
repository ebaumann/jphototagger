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
 * @author Elmar Baumann
 */
public final class XmpMetaDataValueXmpArrayNameMapping {

    private static final Map<MetaDataValue, String> XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE = new HashMap<MetaDataValue, String>();

    static {
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, "dc:creator");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, "dc:description");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, "dc:rights");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, "dc:subject");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, "dc:title");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, "Iptc4xmpCore:DateCreated");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, "Iptc4xmpCore:Location");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, "photoshop:AuthorsPosition");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, "photoshop:CaptionWriter");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, "photoshop:City");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, "photoshop:Country");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCreditMetaDataValue.INSTANCE, "photoshop:Credit");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, "photoshop:Headline");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, "photoshop:Instructions");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, "photoshop:Source");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, "photoshop:State");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, "photoshop:TransmissionReference");
        XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, "xap:Rating");
    }

    public static String getXmpArrayNameOfXmpMetaDataValue(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.get(value);
    }

    /**
     * Finds a value for a string with a specific path start.
     *
     * @param stringArrayNameStart string with a array name start, can contain
     *                             more characters after name start
     * @return                     value or null if not found
     */
    public static MetaDataValue findXmpMetaDataValue(String stringArrayNameStart) {
        if (stringArrayNameStart == null) {
            throw new NullPointerException("stringArrayNameStart == null");
        }

        for (MetaDataValue value : XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.keySet()) {
            if (stringArrayNameStart.startsWith(XMP_ARRAY_NAME_OF_XMP_META_DATA_VALUE.get(value))) {
                return value;
            }
        }

        return null;
    }

    private XmpMetaDataValueXmpArrayNameMapping() {
    }
}
