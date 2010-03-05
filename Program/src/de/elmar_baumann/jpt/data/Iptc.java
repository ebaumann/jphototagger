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
package de.elmar_baumann.jpt.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcRepeatableValues;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * IPTC metadata of an image file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Iptc {

    private final Map<IPTCEntryMeta, Object> valueOfEntryMeta = new HashMap<IPTCEntryMeta, Object>();

    public Object getValue(IPTCEntryMeta iptcEntry) {
        Object value = valueOfEntryMeta.get(iptcEntry);
        return value == null
               ? null
               : value instanceof Collection<?>
               ? new ArrayList<Object>((Collection<?>) value) // Returning a copy
               : value;
    }

    /**
     * Sets the value of an IPTC entry. If the value is repeatable, it will be
     * added to it's collection.
     *
     * @param iptcEntry IPTC entry
     * @param value     value of the entry
     */
    public void setValue(IPTCEntryMeta iptcEntry, Object value) {
        if (IptcRepeatableValues.isRepeatable(iptcEntry)) {
            addToCollection(iptcEntry, value);
        } else {
            valueOfEntryMeta.put(iptcEntry, value);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<? super Object> collectionReference(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof Collection<?>
               ? (Collection<? super Object>) o
               : null;
    }

    @SuppressWarnings("unchecked")
    private void addToCollection(IPTCEntryMeta meta, Object o) {
        if (o == null) return;
        Collection<? super Object> collection = collectionReference(meta);
        if (collection == null) {
            collection = new ArrayList<Object>();
            valueOfEntryMeta.put(meta, collection);
        }
        if (!collection.contains(o)) {
            collection.add(o);
        }
    }
}
