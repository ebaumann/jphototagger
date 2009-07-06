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

    private static final Map<IPTCEntryMeta, Boolean> IS_REPEATABLE = new HashMap<IPTCEntryMeta, Boolean>();


    static {
        IS_REPEATABLE.put(IPTCEntryMeta.BYLINE_TITLE, true);
        IS_REPEATABLE.put(IPTCEntryMeta.BYLINE, true);
        IS_REPEATABLE.put(IPTCEntryMeta.CAPTION_ABSTRACT, false);
        IS_REPEATABLE.put(IPTCEntryMeta.CATEGORY, false);
        IS_REPEATABLE.put(IPTCEntryMeta.CITY, false);
        IS_REPEATABLE.put(IPTCEntryMeta.CONTENT_LOCATION_CODE, true);
        IS_REPEATABLE.put(IPTCEntryMeta.CONTENT_LOCATION_NAME, true);
        IS_REPEATABLE.put(IPTCEntryMeta.COPYRIGHT_NOTICE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME, false);
        IS_REPEATABLE.put(IPTCEntryMeta.CREDIT, false);
        IS_REPEATABLE.put(IPTCEntryMeta.HEADLINE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.KEYWORDS, true);
        IS_REPEATABLE.put(IPTCEntryMeta.OBJECT_NAME, false);
        IS_REPEATABLE.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.PROVINCE_STATE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.SOURCE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, false);
        IS_REPEATABLE.put(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY, true);
        IS_REPEATABLE.put(IPTCEntryMeta.WRITER_EDITOR, true);
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
        Boolean repeatable = IS_REPEATABLE.get(meta);
        if (repeatable == null)
            throw new IllegalArgumentException("Unknown Metadata: " + meta);
        return repeatable;
    }

    private IptcRepeatableValues() {
    }
}
