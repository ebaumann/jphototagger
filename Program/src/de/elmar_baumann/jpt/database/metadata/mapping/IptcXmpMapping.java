/*
 * JPhotoTagger tags and finds images fast.
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

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping between IPTC Entry Metadata and XMP columns.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class IptcXmpMapping {

    private static final Map<IPTCEntryMeta, Column> XMP_COLUMN_OF_IPTC_ENTRY_META = new HashMap<IPTCEntryMeta, Column>();
    private static final Map<Column, IPTCEntryMeta> IPTC_ENTRY_META_OF_XMP_COLUMN = new HashMap<Column, IPTCEntryMeta>();

    static {
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COPYRIGHT_NOTICE               , ColumnXmpDcRights.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CAPTION_ABSTRACT               , ColumnXmpDcDescription.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.OBJECT_NAME                    , ColumnXmpDcTitle.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.HEADLINE                       , ColumnXmpPhotoshopHeadline.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CITY                           , ColumnXmpPhotoshopCity.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.PROVINCE_STATE                 , ColumnXmpPhotoshopState.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME  , ColumnXmpPhotoshopCountry.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS           , ColumnXmpPhotoshopInstructions.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CREDIT                         , ColumnXmpPhotoshopCredit.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SOURCE                         , ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.KEYWORDS                       , ColumnXmpDcSubjectsSubject.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE                         , ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_NAME          , ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.DATE_CREATED                   , ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_CODE          , ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.WRITER_EDITOR                  , ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE_TITLE                   , ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.URGENCY                        , ColumnXmpRating.INSTANCE);

        for (IPTCEntryMeta iptcEntryMeta : XMP_COLUMN_OF_IPTC_ENTRY_META.keySet()) {
            IPTC_ENTRY_META_OF_XMP_COLUMN.put(
                    XMP_COLUMN_OF_IPTC_ENTRY_META.get(iptcEntryMeta), iptcEntryMeta);
        }
    }

    public static Column getXmpColumnOfIptcEntryMeta(IPTCEntryMeta iptcEntryMeta) {
        return XMP_COLUMN_OF_IPTC_ENTRY_META.get(iptcEntryMeta);
    }

    public static IPTCEntryMeta getIptcEntryMetaOfXmpColumn(Column xmpColumn) {
        return IPTC_ENTRY_META_OF_XMP_COLUMN.get(xmpColumn);
    }

    public static List<Pair<IPTCEntryMeta, Column>> getAllPairs() {
        List<Pair<IPTCEntryMeta, Column>> pairs          = new ArrayList<Pair<IPTCEntryMeta, Column>>();
        Set<IPTCEntryMeta>                iptcEntryMetas = XMP_COLUMN_OF_IPTC_ENTRY_META.keySet();

        for (IPTCEntryMeta iptcEntryMeta : iptcEntryMetas) {
            pairs.add(new Pair<IPTCEntryMeta, Column>(
                    iptcEntryMeta, XMP_COLUMN_OF_IPTC_ENTRY_META.get(
                    iptcEntryMeta)));
        }
        return pairs;
    }

    /**
     * Liefert die beiden Spalten gemeinsame Beschreibung: Es wird die IPTC-
     * Beschreibung geliefert ausschließlich " [...".
     *
     * @param  iptcColumn IPTC-Spalte
     * @return Beschreibung
     */
    public static String getCommonDiscription(Column iptcColumn) {
        String description       = iptcColumn.getDescription();
        String commonDescription = description;
        int    index             = description.indexOf("[");

        if (index > 0) {
            commonDescription = description.substring(0, index - 1);
        }
        return commonDescription;
    }

    private IptcXmpMapping() {
    }
}
