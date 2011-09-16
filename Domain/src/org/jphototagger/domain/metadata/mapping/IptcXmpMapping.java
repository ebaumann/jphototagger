package org.jphototagger.domain.metadata.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imagero.reader.iptc.IPTCEntryMeta;

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
 * Mapping between IPTC Entry Metadata and XMP metadata values.
 *
 * @author Elmar Baumann
 */
public final class IptcXmpMapping {

    private static final Map<IPTCEntryMeta, MetaDataValue> XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META = new HashMap<IPTCEntryMeta, MetaDataValue>();
    private static final Map<MetaDataValue, IPTCEntryMeta> IPTC_ENTRY_META_OF_XMP_META_DATA_VALUE = new HashMap<MetaDataValue, IPTCEntryMeta>();

    static {
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COPYRIGHT_NOTICE, XmpDcRightsMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CAPTION_ABSTRACT, XmpDcDescriptionMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.OBJECT_NAME, XmpDcTitleMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.HEADLINE, XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CITY, XmpPhotoshopCityMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.PROVINCE_STATE, XmpPhotoshopStateMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, XmpPhotoshopCountryMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CREDIT, XmpPhotoshopCreditMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SOURCE, XmpPhotoshopSourceMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.KEYWORDS, XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE, XmpDcCreatorMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.DATE_CREATED, XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.WRITER_EDITOR, XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE_TITLE, XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.URGENCY, XmpRatingMetaDataValue.INSTANCE);

        for (IPTCEntryMeta iptcEntryMeta : XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.keySet()) {
            IPTC_ENTRY_META_OF_XMP_META_DATA_VALUE.put(XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.get(iptcEntryMeta), iptcEntryMeta);
        }
    }

    public static MetaDataValue getXmpMetaDataValueOfIptcEntryMeta(IPTCEntryMeta iptcEntryMeta) {
        if (iptcEntryMeta == null) {
            throw new NullPointerException("iptcEntryMeta == null");
        }

        return XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.get(iptcEntryMeta);
    }

    public static IPTCEntryMeta getIptcEntryMetaOfXmpMetaDataValue(MetaDataValue xmpMetaDataValue) {
        if (xmpMetaDataValue == null) {
            throw new NullPointerException("xmpMetaDataValue == null");
        }

        return IPTC_ENTRY_META_OF_XMP_META_DATA_VALUE.get(xmpMetaDataValue);
    }

    public static List<IPTCEntryMetaDataValue> getAllMappings() {
        List<IPTCEntryMetaDataValue> iptcEntryMetaMetaDataValues = new ArrayList<IPTCEntryMetaDataValue>();
        Set<IPTCEntryMeta> iptcEntryMetas = XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.keySet();

        for (IPTCEntryMeta iptcEntryMeta : iptcEntryMetas) {
            iptcEntryMetaMetaDataValues.add(new IPTCEntryMetaDataValue(iptcEntryMeta, XMP_META_DATA_VALUE_OF_IPTC_ENTRY_META.get(iptcEntryMeta)));
        }

        return iptcEntryMetaMetaDataValues;
    }

    private IptcXmpMapping() {
    }
}
