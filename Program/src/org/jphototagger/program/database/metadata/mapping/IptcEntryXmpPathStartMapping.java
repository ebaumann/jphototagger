package org.jphototagger.program.database.metadata.mapping;

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
 * @author Elmar Baumann
 */
public final class IptcEntryXmpPathStartMapping {
    private static final Map<IPTCEntryMeta, String> XMP_PATH_START_OF_IPTC_ENTRY_META =
        new HashMap<IPTCEntryMeta, String>();

    static {
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE,
                "dc:creator");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CAPTION_ABSTRACT,
                "dc:description");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COPYRIGHT_NOTICE,
                "dc:rights");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.KEYWORDS,
                "dc:subject");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.OBJECT_NAME,
                "dc:title");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
            IPTCEntryMeta.CONTENT_LOCATION_CODE, "Iptc4xmpCore:CountryCode");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
            IPTCEntryMeta.CONTENT_LOCATION_NAME, "Iptc4xmpCore:Location");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.DATE_CREATED,
                "Iptc4xmpCore:DateCreated");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE_TITLE,
                "photoshop:AuthorsPosition");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.WRITER_EDITOR,
                "photoshop:CaptionWriter");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CITY,
                "photoshop:City");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
            IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, "photoshop:Country");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CREDIT,
                "photoshop:Credit");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.HEADLINE,
                "photoshop:Headline");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
            IPTCEntryMeta.SPECIAL_INSTRUCTIONS, "photoshop:Instructions");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SOURCE,
                "photoshop:Source");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.PROVINCE_STATE,
                "photoshop:State");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(
            IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE,
            "photoshop:TransmissionReference");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.URGENCY,
                "xap:Rating");
    }

    /**
     * Liefert den Start des XMP-Pfads für IPTC-Entry-Metadaten.
     *
     * @param  entryMeta  IPTC-Entry-Metadaten
     * @return Pfadstart oder null bei unzugeordneten Metadaten
     */
    public static String getXmpPathStartOfIptcEntryMeta(
            IPTCEntryMeta entryMeta) {
        if (entryMeta == null) {
            throw new NullPointerException("entryMeta == null");
        }

        return XMP_PATH_START_OF_IPTC_ENTRY_META.get(entryMeta);
    }

    private IptcEntryXmpPathStartMapping() {}
}
