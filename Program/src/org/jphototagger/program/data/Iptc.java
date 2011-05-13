package org.jphototagger.program.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import org.jphototagger.program.database.metadata.mapping.IptcRepeatableValues;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * IPTC metadata of an image file.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class Iptc {
    private final Map<IPTCEntryMeta, Object> valueOfEntryMeta = new HashMap<IPTCEntryMeta, Object>();

    public Object getValue(IPTCEntryMeta iptcEntry) {
        if (iptcEntry == null) {
            throw new NullPointerException("iptcEntry == null");
        }

        Object value = valueOfEntryMeta.get(iptcEntry);

        return (value == null)
               ? null
               : (value instanceof Collection<?>)
                 ? new ArrayList<Object>((Collection<?>) value)    // Returning a copy
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
        if (iptcEntry == null) {
            throw new NullPointerException("iptcEntry == null");
        }

        if (value == null) {
            valueOfEntryMeta.remove(iptcEntry);

            return;
        }

        if (IptcRepeatableValues.isRepeatable(iptcEntry)) {
            addToCollection(iptcEntry, value);
        } else {
            valueOfEntryMeta.put(iptcEntry, value);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<? super Object> collectionReference(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);

        return (o instanceof Collection<?>)
               ? (Collection<? super Object>) o
               : null;
    }

    @SuppressWarnings("unchecked")
    private void addToCollection(IPTCEntryMeta meta, Object o) {
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
