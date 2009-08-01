package de.elmar_baumann.imv.database.metadata.mapping;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}
 * und dem Start eines
 * {@link com.adobe.xmp.properties.XMPPropertyInfo#getPath()}.
 * 
 * Das Adobe-SDK fügt bei mehrfach vorkommenden Properties einen Index in
 * eckigen Klammern an, weshalb es keine vollständige Abdeckung geben kann.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-19
 */
public final class IptcEntryXmpPathStartMapping {

    private static final Map<IPTCEntryMeta, String> XMP_PATH_START_OF_IPTC_ENTRY_META =
            new HashMap<IPTCEntryMeta, String>();

    static {
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.BYLINE, "dc:creator"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CAPTION_ABSTRACT, "dc:description"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.COPYRIGHT_NOTICE, "dc:rights"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.KEYWORDS, "dc:subject"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.OBJECT_NAME, "dc:title"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CONTENT_LOCATION_CODE, "Iptc4xmpCore:CountryCode"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CONTENT_LOCATION_NAME, "Iptc4xmpCore:Location"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.BYLINE_TITLE, "photoshop:AuthorsPosition"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.WRITER_EDITOR, "photoshop:CaptionWriter"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CATEGORY, "photoshop:Category"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CITY, "photoshop:City"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, "photoshop:Country"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.CREDIT, "photoshop:Credit"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.HEADLINE, "photoshop:Headline"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.SPECIAL_INSTRUCTIONS, "photoshop:Instructions"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.SOURCE, "photoshop:Source"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.PROVINCE_STATE, "photoshop:State"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.SUPPLEMENTAL_CATEGORY,
                "photoshop:SupplementalCategories"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE,
                "photoshop:TransmissionReference"); // NOI18N
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
                IPTCEntryMeta.URGENCY, "xap:Rating"); // NOI18N
    }

    /**
     * Liefert den Start des XMP-Pfads für IPTC-Entry-Metadaten.
     * 
     * @param  entryMeta  IPTC-Entry-Metadaten
     * @return Pfadstart oder null bei unzugeordneten Metadaten
     */
    public static String getXmpPathStartOfIptcEntryMeta(IPTCEntryMeta entryMeta) {
        return XMP_PATH_START_OF_IPTC_ENTRY_META.get(entryMeta);
    }

    private IptcEntryXmpPathStartMapping() {
    }
}
