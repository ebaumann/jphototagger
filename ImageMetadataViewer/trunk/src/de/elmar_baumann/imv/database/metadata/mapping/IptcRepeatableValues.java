package de.elmar_baumann.imv.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns whether an {@link com.imagero.reader.iptc.IPTCEntryMeta} contains
 * repeatable values.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/20
 */
public final class IptcRepeatableValues {

    private static final Map<IPTCEntryMeta, Boolean> repeatableOf = new HashMap<IPTCEntryMeta, Boolean>();


    static {
        repeatableOf.put(IPTCEntryMeta.BYLINE_TITLE, true);
        repeatableOf.put(IPTCEntryMeta.BYLINE, true);
        repeatableOf.put(IPTCEntryMeta.CAPTION_ABSTRACT, false);
        repeatableOf.put(IPTCEntryMeta.CATEGORY, false);
        repeatableOf.put(IPTCEntryMeta.CITY, false);
        repeatableOf.put(IPTCEntryMeta.CONTENT_LOCATION_CODE, true);
        repeatableOf.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, true);
        repeatableOf.put(IPTCEntryMeta.COPYRIGHT_NOTICE, false);
        repeatableOf.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, false);
        repeatableOf.put(IPTCEntryMeta.CREDIT, false);
        repeatableOf.put(IPTCEntryMeta.HEADLINE, false);
        repeatableOf.put(IPTCEntryMeta.KEYWORDS, true);
        repeatableOf.put(IPTCEntryMeta.OBJECT_NAME, false);
        repeatableOf.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, false);
        repeatableOf.put(IPTCEntryMeta.PROVINCE_STATE, false);
        repeatableOf.put(IPTCEntryMeta.SOURCE, false);
        repeatableOf.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, false);
        repeatableOf.put(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY, true);
        repeatableOf.put(IPTCEntryMeta.WRITER_EDITOR, true);
    }

    /**
     * Returns whether an {@link com.imagero.reader.iptc.IPTCEntryMeta} contains
     * repeatable values.
     * 
     * @param  meta metadata
     * @return true if repeatable
     * @throws IllegalArgumentException if metadata is unknown
     */
    public static boolean isRepeatable(IPTCEntryMeta meta) {
        Boolean repeatable = repeatableOf.get(meta);
        if (repeatable == null)
            throw new IllegalArgumentException("Unknown Metadata: " + meta);
        return repeatable;
    }

    private IptcRepeatableValues() {
    }
}
