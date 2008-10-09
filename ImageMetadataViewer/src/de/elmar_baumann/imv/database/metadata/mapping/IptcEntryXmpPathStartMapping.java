package de.elmar_baumann.imv.database.metadata.mapping;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.HashMap;

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
 * @version 2008/09/19
 */
public class IptcEntryXmpPathStartMapping {

    private static HashMap<IPTCEntryMeta, String> xmpPathStarOfIptcEntryMeta = new HashMap<IPTCEntryMeta, String>();
    private static IptcEntryXmpPathStartMapping instance = new IptcEntryXmpPathStartMapping();
    

    static {
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.BYLINE, "dc:creator"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CAPTION_ABSTRACT, "dc:description"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.COPYRIGHT_NOTICE, "dc:rights"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.KEYWORDS, "dc:subject"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.OBJECT_NAME, "dc:title"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CONTENT_LOCATION_CODE, "Iptc4xmpCore:CountryCode"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, "Iptc4xmpCore:Location"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.BYLINE_TITLE, "photoshop:AuthorsPosition"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.WRITER_EDITOR, "photoshop:CaptionWriter"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CATEGORY, "photoshop:Category"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CITY, "photoshop:City"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, "photoshop:Country"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.CREDIT, "photoshop:Credit"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.HEADLINE, "photoshop:Headline"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, "photoshop:Instructions"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.SOURCE, "photoshop:Source"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.PROVINCE_STATE, "photoshop:State"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY, "photoshop:SupplementalCategories"); // NOI18N
        xmpPathStarOfIptcEntryMeta.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, "photoshop:TransmissionReference"); // NOI18N
    }

    public static IptcEntryXmpPathStartMapping getInstance() {
        return instance;
    }

    private IptcEntryXmpPathStartMapping() {
    }

    /**
     * Liefert den Start des XMP-Pfads für IPTC-Entry-Metadaten.
     * 
     * @param  entryMeta  IPTC-Entry-Metadaten
     * @return Pfadstart oder null bei unzugeordneten Metadaten
     */
    public String getXmpPathStartOfIptcEntryMeta(IPTCEntryMeta entryMeta) {
        return xmpPathStarOfIptcEntryMeta.get(entryMeta);
    }
}
