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

import com.adobe.xmp.XMPConst;

/**
 * Mapping zwischen
 * {@link org.jphototagger.program.database.metadata.MetaDataValue} und
 * einem Namespace-URI
 *
 * @author Elmar Baumann
 */
public final class XmpMetaDataValuesNamespaceUriMapping {

    private static final Map<MetaDataValue, String> NAMESPACE_URI_OF_XMP_METADATA_VALUE = new HashMap<MetaDataValue, String>();

    static {
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopCreditMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_XMP_METADATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, XMPConst.NS_XMP);
    }

    public static String getNamespaceUriOfXmpMetaDataValue(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return NAMESPACE_URI_OF_XMP_METADATA_VALUE.get(value);
    }

    private XmpMetaDataValuesNamespaceUriMapping() {
    }
}
