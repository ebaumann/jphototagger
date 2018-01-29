package org.jphototagger.domain.metadata.xmp;

import com.imagero.reader.iptc.IPTCEntryMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.iptc.Iptc;
import org.jphototagger.domain.metadata.mapping.IPTCEntryMetaDataValue;
import org.jphototagger.domain.metadata.mapping.IptcXmpMapping;
import org.jphototagger.domain.metadata.mapping.XmpRepeatableValues;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * XMP metadata of an image file. The <code>see</code> sections of the method
 * documentation links to the corresponding {@code Iptc} method.
 *
 * @author Elmar Baumann
 */
public final class Xmp implements TextEntryListener {

    private final Map<MetaDataValue, Object> metaDataValue = new HashMap<>();

    public Xmp() {
    }

    public Xmp(Xmp other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        set(other);
    }

    public boolean contains(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        return metaDataValue.get(value) != null;
    }

    public Object remove(MetaDataValue mdValue) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }
        return metaDataValue.remove(mdValue);
    }

    @Override
    public void textRemoved(final MetaDataValue mdValue, final String removedText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                removeValue(mdValue, removedText);
            }
        });
    }

    @Override
    public void textAdded(final MetaDataValue mdValue, final String addedText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                setValue(mdValue, addedText);
            }
        });
    }

    @Override
    public void textChanged(final MetaDataValue mdValue, final String oldText, final String newText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                changeText(mdValue, newText, oldText);
            }
        });
    }

    private void changeText(MetaDataValue mdValue, String newText, String oldText) {
        if (XmpRepeatableValues.isRepeatable(mdValue)) {
            Object o = metaDataValue.get(mdValue);
            if (o == null) {
                Collection<Object> collection = new ArrayList<>();
                collection.add(newText);
                metaDataValue.put(mdValue, collection);
            } else if (o instanceof Collection<?>) {
                @SuppressWarnings(value = "unchecked") Collection<? super Object> collection = (Collection<? super Object>) o;
                collection.remove(oldText);
                collection.add(newText);
            }
        } else {
            setValue(mdValue, newText);
        }
    }

    public void setMetaDataTemplate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        for (MetaDataValue value : XmpMetaDataValues.get()) {
            metaDataValue.put(value, template.getMetaDataValue(value));
        }
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean containsValue(MetaDataValue mdValue, Object value) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        Object o = metaDataValue.get(mdValue);
        if (o == null) {
            return false;
        }
        if (o instanceof Collection<?>) {
            return ((Collection<?>) o).contains(value);
        } else {
            return o.equals(value);
        }
    }

    /**
     * Loops over {@link #containsValue(org.jphototagger.domain.metadata.MetaDataValue, java.lang.Object)}
     * @param mdValue
     * @param values
     * @return true, if a value in vales is contained
     */
    public boolean containsOneOf(MetaDataValue mdValue, Collection<?> values) {
        for (Object value : values) {
            if (containsValue(mdValue, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param oldXmp
     * @param updatedXmp
     * @param mdValue
     * @param values
     * @return true, if one of the values is present in {@code updatedXmp} but not in {@link oldXmp}
     */
    public static boolean valueInserted(Xmp oldXmp, Xmp updatedXmp, MetaDataValue mdValue, Collection<?> values) {
        for (Object value : values) {
            if (!oldXmp.containsValue(mdValue, value) && updatedXmp.containsValue(mdValue, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param oldXmp
     * @param updatedXmp
     * @param mdValue
     * @param values
     * @return true, if one of the values is present in {@code oldXmp} but not in {@link updatedXmp}
     */
    public static boolean valueDeleted(Xmp oldXmp, Xmp updatedXmp, MetaDataValue mdValue, Collection<?> values) {
        for (Object value : values) {
            if (oldXmp.containsValue(mdValue, value) && !updatedXmp.containsValue(mdValue, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * How to set IPTC values.
     */
    public enum SetIptc {

        /** Replace existing XMP values with existing IPTC values */
        REPLACE_EXISTING_VALUES,
        /**
         * Don't replace existing XMP values with existing IPTC values but
         * add IPTC values to repeatable XMP values
         */
        DONT_CHANGE_EXISTING_VALUES
    };

    public void setIptc(Iptc iptc, SetIptc options) {
        if (iptc == null) {
            throw new NullPointerException("iptc == null");
        }
        if (options == null) {
            throw new NullPointerException("options == null");
        }
        if (options.equals(SetIptc.REPLACE_EXISTING_VALUES)) {
            clear();
        }
        List<IPTCEntryMetaDataValue> mappings = IptcXmpMapping.getAllMappings();
        for (IPTCEntryMetaDataValue mapping : mappings) {
            MetaDataValue xmpMetaDataValue = mapping.getMetaDataValue();
            IPTCEntryMeta iptcEntryMeta = IptcXmpMapping.getIptcEntryMetaOfXmpMetaDataValue(xmpMetaDataValue);
            Object iptcValue = iptc.getValue(iptcEntryMeta);
            if (iptcValue != null) {
                if (iptcValue instanceof String) {
                    String iptcString = (String) iptcValue;
                    boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES) || (getValue(xmpMetaDataValue) == null);
                    if (isSet) {
                        iptcString = formatIptcDate(xmpMetaDataValue, iptcString);
                        setValue(xmpMetaDataValue, iptcString);
                    }
                } else if (iptcValue instanceof Collection<?>) {
                    @SuppressWarnings("unchecked") Collection<?> collection = (Collection<?>) iptcValue;

                    if (XmpRepeatableValues.isRepeatable(xmpMetaDataValue)) {
                        for (Object o : collection) {
                            setValue(xmpMetaDataValue, o);
                        }
                    } else if (!collection.isEmpty()) {
                        boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES)
                                || (getValue(xmpMetaDataValue) == null);
                        if (isSet) {
                            int i = 0;

                            for (Object value : collection) {
                                if (i++ == 0) {
                                    setValue(xmpMetaDataValue, value);
                                }
                            }
                        }
                    }
                } else {
                    Logger.getLogger(Xmp.class.getName()).log(Level.WARNING, "Error setting iptc{0} for {1}", new Object[]{iptcValue, xmpMetaDataValue});
                }
            }
        }
    }

    private String formatIptcDate(MetaDataValue xmpMetaDataValue, String iptcString) {
        if (iptcString == null) {
            return null;
        }

        if (xmpMetaDataValue.equals(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE) && (iptcString.length() == 8)) {
            if (iptcString.contains("-")) {
                return iptcString;
            }
            return iptcString.substring(0, 4) + "-" + iptcString.substring(4, 6) + "-" + iptcString.substring(6);
        }
        return iptcString;
    }

    /**
     * @param xmpMetaDataValue
     * @return value or null if not contained. Repeatable values returned as list, if the value is not contained,
     * an empty list will be returned
     */
    public Object getValue(MetaDataValue xmpMetaDataValue) {
        if (xmpMetaDataValue == null) {
            throw new NullPointerException("xmpMetaDataValue == null");
        }
        Object o = metaDataValue.get(xmpMetaDataValue);
        return (o instanceof Collection<?>)
                ? new ArrayList<>((Collection<?>) o)
                : o;
    }

    public void setValue(MetaDataValue xmpMetaDataValue, Object value) {
        if (xmpMetaDataValue == null) {
            throw new NullPointerException("xmpMetaDataValue == null");
        }
        if (value == null) {
            metaDataValue.remove(xmpMetaDataValue);
            return;
        }
        if (XmpRepeatableValues.isRepeatable(xmpMetaDataValue)) {
            addToCollection(xmpMetaDataValue, value);
        } else {
            metaDataValue.put(xmpMetaDataValue, value);
        }
    }

    /**
     * Removes a value of a XMP metadata value.
     *
     * If the value contains a repeatable value it will be removed from it's
     * collection.
     *
     * @param xmpMetaDataValue
     * @param value     value not null
     */
    public void removeValue(MetaDataValue xmpMetaDataValue, Object value) {
        if (xmpMetaDataValue == null) {
            throw new NullPointerException("xmpMetaDataValue == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        Object o = metaDataValue.get(xmpMetaDataValue);
        boolean remove = true;
        if (o instanceof Collection<?>) {
            @SuppressWarnings("unchecked") Collection<?> collection = (Collection<?>) o;
            if (xmpMetaDataValue == XmpDcSubjectsSubjectMetaDataValue.INSTANCE && value instanceof String) {
                removeXmpDcSubject(collection, (String) value);
            } else {
                collection.remove(value);
            }
            remove = collection.isEmpty();
        }
        if (remove) {
            metaDataValue.remove(xmpMetaDataValue);
        }
    }

    // DC subjectes are case insensitive. When "to" should be removed, then
    // "TO", "tO" and "To" also have to be removed.
    private void removeXmpDcSubject(Collection<?> collection, String dcSubject) {
        if (!StringUtil.hasContent(dcSubject)) {
            return;
        }
        String dcSubjectLc = dcSubject.toLowerCase();
        for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
            Object element = it.next();
            if (element instanceof String) {
                String existingDcSubject = (String) element;
                if (dcSubjectLc.equals(existingDcSubject.toLowerCase())) {
                    it.remove();
                }
            }
        }
    }

    public void clear() {
        metaDataValue.clear();
    }

    public boolean isEmpty() {
        for (MetaDataValue value : metaDataValue.keySet()) {
            Object o = metaDataValue.get(value);
            if (o instanceof String) {
                String string = (String) o;
                if (!string.trim().isEmpty()) {
                    return false;
                }
            } else if (o instanceof List<?>) {
                List<?> list = (List<?>) o;
                if (!list.isEmpty()) {
                    return false;
                }
            } else if (o != null) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Collection<? super Object> collectionReferenceOf(MetaDataValue value) {
        Object o = metaDataValue.get(value);
        return (o instanceof Collection<?>)
                ? (Collection<? super Object>) o
                : null;
    }

    private void addToCollection(MetaDataValue mdValue, Object value) {
        Collection<? super Object> collection = collectionReferenceOf(mdValue);
        if (collection == null) {
            collection = new ArrayList<>();
            metaDataValue.put(mdValue, collection);
        }
        Collection<?> values;
        if (value instanceof Collection<?>) {
            values = (Collection<?>) value;
        } else {
            values = Arrays.asList(value);
        }
        for (Object v : values) {
            if (!collection.contains(v)) {
                collection.add(v);
            }
        }
    }

    public void set(Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }
        if (xmp == this) {
            return;
        }
        metaDataValue.clear();
        for (MetaDataValue value : xmp.metaDataValue.keySet()) {
            Object o = xmp.metaDataValue.get(value);
            if (o instanceof Collection<?>) {
                metaDataValue.put(value, new ArrayList<>((List<?>) o));
            } else if (o != null) {
                metaDataValue.put(value, o);
            }
        }
    }

    @Override
    public String toString() {
        return metaDataValue.toString();
    }
}
