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
import java.util.HashMap;
import java.util.Map;

/**
 * Returns whether an {@link com.imagero.reader.iptc.IPTCEntryMeta} contains
 * repeatable values.
 *
 * @author  Elmar Baumann
 * @version 2009-02-20
 */
public final class IptcRepeatableValues {

    private static final Map<IPTCEntryMeta, Boolean> IS_REPEATABLE = new HashMap<IPTCEntryMeta, Boolean>();

    static {
        IS_REPEATABLE.put(IPTCEntryMeta.BYLINE_TITLE                   , true);
        IS_REPEATABLE.put(IPTCEntryMeta.BYLINE                         , true);
        IS_REPEATABLE.put(IPTCEntryMeta.CAPTION_ABSTRACT               , false);
        IS_REPEATABLE.put(IPTCEntryMeta.CITY                           , false);
        IS_REPEATABLE.put(IPTCEntryMeta.CONTENT_LOCATION_CODE          , true);
        IS_REPEATABLE.put(IPTCEntryMeta.CONTENT_LOCATION_NAME          , true);
        IS_REPEATABLE.put(IPTCEntryMeta.COPYRIGHT_NOTICE               , false);
        IS_REPEATABLE.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME  , false);
        IS_REPEATABLE.put(IPTCEntryMeta.CREDIT                         , false);
        IS_REPEATABLE.put(IPTCEntryMeta.HEADLINE                       , false);
        IS_REPEATABLE.put(IPTCEntryMeta.KEYWORDS                       , true);
        IS_REPEATABLE.put(IPTCEntryMeta.OBJECT_NAME                    , false);
        IS_REPEATABLE.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE, false);
        IS_REPEATABLE.put(IPTCEntryMeta.PROVINCE_STATE                 , false);
        IS_REPEATABLE.put(IPTCEntryMeta.SOURCE                         , false);
        IS_REPEATABLE.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS           , false);
        IS_REPEATABLE.put(IPTCEntryMeta.WRITER_EDITOR                  , true);
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

        return repeatable == null
                ? false
                : repeatable;
    }

    private IptcRepeatableValues() {
    }
}
