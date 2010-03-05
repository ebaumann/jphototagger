/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata.mapping;

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

    private static final Map<IPTCEntryMeta, String> XMP_PATH_START_OF_IPTC_ENTRY_META = new HashMap<IPTCEntryMeta, String>();

    static {
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE                         , "dc:creator");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CAPTION_ABSTRACT               , "dc:description");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COPYRIGHT_NOTICE               , "dc:rights");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.KEYWORDS                       , "dc:subject");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.OBJECT_NAME                    , "dc:title");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_CODE          , "Iptc4xmpCore:CountryCode");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_NAME          , "Iptc4xmpCore:Location");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.DATE_CREATED                   , "Iptc4xmpCore:DateCreated");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE_TITLE                   , "photoshop:AuthorsPosition");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.WRITER_EDITOR                  , "photoshop:CaptionWriter");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CITY                           , "photoshop:City");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME  , "photoshop:Country");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CREDIT                         , "photoshop:Credit");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.HEADLINE                       , "photoshop:Headline");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS           , "photoshop:Instructions");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SOURCE                         , "photoshop:Source");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.PROVINCE_STATE                 , "photoshop:State");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, "photoshop:TransmissionReference");
        XMP_PATH_START_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.URGENCY                        , "xap:Rating");
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
