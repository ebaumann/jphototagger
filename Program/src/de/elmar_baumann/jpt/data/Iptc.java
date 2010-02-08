/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcRepeatableValues;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IPTC metadata of an image file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Iptc {

    private final Map<IPTCEntryMeta, Object> valueOfEntryMeta = new HashMap<IPTCEntryMeta, Object>();
    /**
     * Returns the value of an IPTC entry.
     *
     * @param  iptcEntry IPTC entry
     * @return           value. It's a string for not repeatable values or a
     *                   string list for repeatable values or null if this entry
     *                   has no value.
     */
    @SuppressWarnings("unchecked")
    public Object getValue(IPTCEntryMeta iptcEntry) {
        Object value = valueOfEntryMeta.get(iptcEntry);
        assert value == null || value instanceof List<?> || value instanceof String :
                "Neither List nor String: " + value;
        return value == null
               ? null
               : value instanceof List<?>
                 ? new ArrayList<String>((List<String>) value)
                 : value instanceof String
                   ? value
                   : null;
    }

    /**
     * Sets the value of an IPTC entry. If the value is repeatable, it will be
     * added to it's array.
     *
     * @param iptcEntry IPTC entry
     * @param value     value of the entry
     */
    public void setValue(IPTCEntryMeta iptcEntry, String value) {
        if (IptcRepeatableValues.isRepeatable(iptcEntry)) {
            addToStringList(iptcEntry, value);
        } else {
            valueOfEntryMeta.put(iptcEntry, value);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> stringListOf(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof List<?>
               ? (List<String>) o
               : null;
    }

    @SuppressWarnings("unchecked")
    private void addToStringList(IPTCEntryMeta meta, String string) {
        if (string == null) return;
        List<String> list = stringListOf(meta);
        if (list == null) {
            list = new ArrayList<String>();
            valueOfEntryMeta.put(meta, list);
        }
        if (!list.contains(string)) {
            list.add(string);
        }
    }
}
