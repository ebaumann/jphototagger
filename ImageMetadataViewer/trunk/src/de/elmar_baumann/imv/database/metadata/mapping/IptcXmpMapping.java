package de.elmar_baumann.imv.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.lib.template.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping between IPTC Entry Metadata and XMP columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class IptcXmpMapping {

    private static final Map<IPTCEntryMeta, Column> xmpColumnOfIptcEntryMeta = new HashMap<IPTCEntryMeta, Column>();
    private static final Map<Column, IPTCEntryMeta> iptcEntryMetaOfXmpColumn = new HashMap<Column, IPTCEntryMeta>();
    

    static {
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.COPYRIGHT_NOTICE, ColumnXmpDcRights.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CAPTION_ABSTRACT, ColumnXmpDcDescription.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.OBJECT_NAME, ColumnXmpDcTitle.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.HEADLINE, ColumnXmpPhotoshopHeadline.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CATEGORY, ColumnXmpPhotoshopCategory.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CITY, ColumnXmpPhotoshopCity.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.PROVINCE_STATE, ColumnXmpPhotoshopState.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, ColumnXmpPhotoshopCountry.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, ColumnXmpPhotoshopInstructions.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CREDIT, ColumnXmpPhotoshopCredit.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.SOURCE, ColumnXmpPhotoshopSource.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.KEYWORDS, ColumnXmpDcSubjectsSubject.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.BYLINE, ColumnXmpDcCreator.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.CONTENT_LOCATION_CODE, ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.WRITER_EDITOR, ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY, ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        xmpColumnOfIptcEntryMeta.put(IPTCEntryMeta.BYLINE_TITLE, ColumnXmpPhotoshopAuthorsposition.INSTANCE);

        for (IPTCEntryMeta iptcEntryMeta : xmpColumnOfIptcEntryMeta.keySet()) {
            iptcEntryMetaOfXmpColumn.put(xmpColumnOfIptcEntryMeta.get(iptcEntryMeta), iptcEntryMeta);
        }
    }

    public static Column getXmpColumnOfIptcEntryMeta(IPTCEntryMeta iptcEntryMeta) {
        return xmpColumnOfIptcEntryMeta.get(iptcEntryMeta);
    }

    public static IPTCEntryMeta getIptcEntryMetaOfXmpColumn(Column xmpColumn) {
        return iptcEntryMetaOfXmpColumn.get(xmpColumn);
    }

    public static List<Pair<IPTCEntryMeta, Column>> getAllPairs() {
        List<Pair<IPTCEntryMeta, Column>> pairs = new ArrayList<Pair<IPTCEntryMeta, Column>>();
        Set<IPTCEntryMeta> iptcEntryMetas = xmpColumnOfIptcEntryMeta.keySet();
        for (IPTCEntryMeta iptcEntryMeta : iptcEntryMetas) {
            pairs.add(new Pair<IPTCEntryMeta, Column>(
                iptcEntryMeta, xmpColumnOfIptcEntryMeta.get(iptcEntryMeta)));
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
        String description = iptcColumn.getDescription();
        String commonDescription = description;
        int index = description.indexOf("["); // NOI18N
        if (index > 0) {
            commonDescription = description.substring(0, index - 1);
        }
        return commonDescription;
    }

    private IptcXmpMapping() {
    }
}
