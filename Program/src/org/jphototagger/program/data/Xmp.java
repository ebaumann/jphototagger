package org.jphototagger.program.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.mapping.IptcXmpMapping;
import org.jphototagger.program.database.metadata.mapping.XmpRepeatableValues;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.event.listener.TextEntryListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * XMP metadata of an image file. The <code>see</code> sections of the method
 * documentation links to the corresponding {@link Iptc} method.
 *
 * @author Elmar Baumann
 */
public final class Xmp implements TextEntryListener {
    private final Map<Column, Object> valueOfColumn = new HashMap<Column, Object>();

    public Xmp() {}

    public Xmp(Xmp other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        set(other);
    }

    public boolean contains(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        return valueOfColumn.get(xmpColumn) != null;
    }

    public Object remove(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        return valueOfColumn.remove(xmpColumn);
    }

    @Override
    public void textRemoved(final Column column, final String removedText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                removeValue(column, removedText);
            }
        });
    }

    @Override
    public void textAdded(final Column column, final String addedText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                setValue(column, addedText);
            }
        });
    }

    @Override
    public void textChanged(final Column xmpColumn, final String oldText, final String newText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                changeText(xmpColumn, newText, oldText);
            }
        });
    }

    private void changeText(Column xmpColumn, String newText, String oldText) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            Object o = valueOfColumn.get(xmpColumn);

            if (o == null) {
                Collection<Object> collection = new ArrayList<Object>();

                collection.add(newText);
                valueOfColumn.put(xmpColumn, collection);
            } else if (o instanceof Collection<?>) {
                @SuppressWarnings(
                    value = "unchecked") Collection<? super Object> collection = (Collection<? super Object>) o;

                collection.remove(oldText);
                collection.add(newText);
            }
        } else {
            setValue(xmpColumn, newText);
        }
    }

    public void setMetaDataTemplate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        for (Column column : XmpColumns.get()) {
            valueOfColumn.put(column, template.getValueOfColumn(column));
        }
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean containsValue(Column column, Object value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        Object o = valueOfColumn.get(column);

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
    }

    ;
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

        List<Pair<IPTCEntryMeta, Column>> mappings = IptcXmpMapping.getAllPairs();

        for (Pair<IPTCEntryMeta, Column> mappingPair : mappings) {
            Column xmpColumn = mappingPair.getSecond();
            IPTCEntryMeta iptcEntryMeta = IptcXmpMapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            Object iptcValue = iptc.getValue(iptcEntryMeta);

            if (iptcValue != null) {
                if (iptcValue instanceof String) {
                    String iptcString = (String) iptcValue;
                    boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES) || (getValue(xmpColumn) == null);

                    if (isSet) {
                        iptcString = formatIptcDate(xmpColumn, iptcString);
                        setValue(xmpColumn, iptcString);
                    }
                } else if (iptcValue instanceof Collection<?>) {
                    @SuppressWarnings("unchecked") Collection<?> collection = (Collection<?>) iptcValue;

                    if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
                        for (Object o : collection) {
                            setValue(xmpColumn, o);
                        }
                    } else if (!collection.isEmpty()) {
                        boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES)
                                        || (getValue(xmpColumn) == null);

                        if (isSet) {
                            int i = 0;

                            for (Object value : collection) {
                                if (i++ == 0) {
                                    setValue(xmpColumn, value);
                                }
                            }
                        }
                    }
                } else {
                    AppLogger.logWarning(Xmp.class, "Xmp.Error.SetIptc", iptcValue, xmpColumn);
                }
            }
        }
    }

    private String formatIptcDate(Column xmpColumn, String iptcString) {
        if (iptcString == null) {
            return null;
        }

        if (xmpColumn.equals(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE) && (iptcString.length() == 8)) {
            if (iptcString.contains("-")) {
                return iptcString;
            }

            return iptcString.substring(0, 4) + "-" + iptcString.substring(4, 6) + "-" + iptcString.substring(6);
        }

        return iptcString;
    }

    public Object getValue(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        Object o = valueOfColumn.get(xmpColumn);

        return (o instanceof Collection<?>)
               ? new ArrayList<Object>((Collection<?>) o)
               : o;
    }

    public void setValue(Column xmpColumn, Object value) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        if (value == null) {
            valueOfColumn.remove(xmpColumn);

            return;
        }

        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            addToCollection(xmpColumn, value);
        } else {
            valueOfColumn.put(xmpColumn, value);
        }
    }

    /**
     * Removes a value of a XMP column.
     *
     * If the column contains a repeatable value it will be removed from it's
     * collection.
     *
     * @param xmpColumn XMP column
     * @param value     value not null
     */
    public void removeValue(Column xmpColumn, Object value) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        Object o = valueOfColumn.get(xmpColumn);
        boolean remove = true;

        if (o instanceof Collection<?>) {
            @SuppressWarnings("unchecked") Collection<?> collection = (Collection<?>) o;

            collection.remove(value);
            remove = collection.isEmpty();
        }

        if (remove) {
            valueOfColumn.remove(xmpColumn);
        }
    }

    public void clear() {
        valueOfColumn.clear();
    }

    public boolean isEmpty() {
        for (Column column : valueOfColumn.keySet()) {
            Object o = valueOfColumn.get(column);

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
    private Collection<? super Object> collectionReferenceOf(Column column) {
        Object o = valueOfColumn.get(column);

        return (o instanceof Collection<?>)
               ? (Collection<? super Object>) o
               : null;
    }

    private void addToCollection(Column column, Object value) {
        Collection<? super Object> collection = collectionReferenceOf(column);

        if (collection == null) {
            collection = new ArrayList<Object>();
            valueOfColumn.put(column, collection);
        }

        Collection<?> values = null;

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

        valueOfColumn.clear();

        for (Column column : xmp.valueOfColumn.keySet()) {
            Object o = xmp.valueOfColumn.get(column);

            if (o instanceof Collection<?>) {
                valueOfColumn.put(column, new ArrayList<Object>((List<?>) o));
            } else if (o != null) {
                valueOfColumn.put(column, o);
            }
        }
    }

    @Override
    public String toString() {
        return valueOfColumn.toString();
    }
}
