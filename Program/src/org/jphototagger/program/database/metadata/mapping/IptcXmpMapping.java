package org.jphototagger.program.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.domain.Column;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping between IPTC Entry Metadata and XMP columns.
 *
 * @author Elmar Baumann
 */
public final class IptcXmpMapping {
    private static final Map<IPTCEntryMeta, Column> XMP_COLUMN_OF_IPTC_ENTRY_META = new HashMap<IPTCEntryMeta,
                                                                                        Column>();
    private static final Map<Column, IPTCEntryMeta> IPTC_ENTRY_META_OF_XMP_COLUMN = new HashMap<Column,
                                                                                        IPTCEntryMeta>();

    static {
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COPYRIGHT_NOTICE, ColumnXmpDcRights.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CAPTION_ABSTRACT, ColumnXmpDcDescription.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.OBJECT_NAME, ColumnXmpDcTitle.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.HEADLINE, ColumnXmpPhotoshopHeadline.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CITY, ColumnXmpPhotoshopCity.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.PROVINCE_STATE, ColumnXmpPhotoshopState.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME,
                                          ColumnXmpPhotoshopCountry.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE,
                                          ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, ColumnXmpPhotoshopInstructions.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CREDIT, ColumnXmpPhotoshopCredit.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.SOURCE, ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.KEYWORDS, ColumnXmpDcSubjectsSubject.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE, ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.DATE_CREATED, ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.WRITER_EDITOR, ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.BYLINE_TITLE, ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        XMP_COLUMN_OF_IPTC_ENTRY_META.put(IPTCEntryMeta.URGENCY, ColumnXmpRating.INSTANCE);

        for (IPTCEntryMeta iptcEntryMeta : XMP_COLUMN_OF_IPTC_ENTRY_META.keySet()) {
            IPTC_ENTRY_META_OF_XMP_COLUMN.put(XMP_COLUMN_OF_IPTC_ENTRY_META.get(iptcEntryMeta), iptcEntryMeta);
        }
    }

    public static Column getXmpColumnOfIptcEntryMeta(IPTCEntryMeta iptcEntryMeta) {
        if (iptcEntryMeta == null) {
            throw new NullPointerException("iptcEntryMeta == null");
        }

        return XMP_COLUMN_OF_IPTC_ENTRY_META.get(iptcEntryMeta);
    }

    public static IPTCEntryMeta getIptcEntryMetaOfXmpColumn(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        return IPTC_ENTRY_META_OF_XMP_COLUMN.get(xmpColumn);
    }

    public static List<Pair<IPTCEntryMeta, Column>> getAllPairs() {
        List<Pair<IPTCEntryMeta, Column>> pairs = new ArrayList<Pair<IPTCEntryMeta, Column>>();
        Set<IPTCEntryMeta> iptcEntryMetas = XMP_COLUMN_OF_IPTC_ENTRY_META.keySet();

        for (IPTCEntryMeta iptcEntryMeta : iptcEntryMetas) {
            pairs.add(new Pair<IPTCEntryMeta, Column>(iptcEntryMeta, XMP_COLUMN_OF_IPTC_ENTRY_META.get(iptcEntryMeta)));
        }

        return pairs;
    }

    private IptcXmpMapping() {}
}
