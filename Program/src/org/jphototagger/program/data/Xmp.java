/*
 * @(#)Xmp.java    Created on 2008-08-22
 *
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

package org.jphototagger.program.data;

import com.imagero.reader.iptc.IPTCEntryMeta;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.mapping.IptcXmpMapping;
import org.jphototagger.program.database.metadata.mapping.XmpRepeatableValues;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.event.listener.TextEntryListener;
import org.jphototagger.lib.generics.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XMP metadata of an image file. The <code>see</code> sections of the method
 * documentation links to the corresponding {@link Iptc} method.
 *
 * @author  Elmar Baumann
 */
public final class Xmp implements TextEntryListener {
    private final Map<Column, Object> valueOfColumn = new HashMap<Column,
                                                          Object>();

    public Xmp() {}

    public Xmp(Xmp other) {
        set(other);
    }

    public boolean contains(Column xmpColumn) {
        return valueOfColumn.get(xmpColumn) != null;
    }

    public Object remove(Column xmpColumn) {
        return valueOfColumn.remove(xmpColumn);
    }

    @Override
    public void textRemoved(Column column, String removedText) {
        removeValue(column, removedText);
    }

    @Override
    public void textAdded(Column column, String addedText) {
        setValue(column, addedText);
    }

    @Override
    public void textChanged(Column xmpColumn, String oldText, String newText) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            Object o = valueOfColumn.get(xmpColumn);

            if (o == null) {
                Collection<Object> collection = new ArrayList<Object>();

                collection.add(newText);
                valueOfColumn.put(xmpColumn, collection);
            } else if (o instanceof Collection<?>) {
                @SuppressWarnings(
                    "unchecked") Collection<? super Object> collection =
                        (Collection<? super Object>) o;

                collection.remove(oldText);
                collection.add(newText);
            }
        } else {
            setValue(xmpColumn, newText);
        }
    }

    public void setMetaDataTemplate(MetadataTemplate template) {
        for (Column column : XmpColumns.get()) {
            valueOfColumn.put(column, template.getValueOfColumn(column));
        }
    }

    @SuppressWarnings("element-type-mismatch")
    public boolean containsValue(Column column, Object value) {
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
        if (options.equals(SetIptc.REPLACE_EXISTING_VALUES)) {
            clear();
        }

        List<Pair<IPTCEntryMeta, Column>> mappings =
            IptcXmpMapping.getAllPairs();

        for (Pair<IPTCEntryMeta, Column> mappingPair : mappings) {
            Column        xmpColumn     = mappingPair.getSecond();
            IPTCEntryMeta iptcEntryMeta =
                IptcXmpMapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            Object iptcValue = iptc.getValue(iptcEntryMeta);

            if (iptcValue != null) {
                if (iptcValue instanceof String) {
                    String  iptcString = (String) iptcValue;
                    boolean isSet      =
                        options.equals(SetIptc.REPLACE_EXISTING_VALUES)
                        || (getValue(xmpColumn) == null);

                    if (isSet) {
                        iptcString = formatIptcDate(xmpColumn, iptcString);
                        setValue(xmpColumn, iptcString);
                    }
                } else if (iptcValue instanceof Collection<?>) {
                    @SuppressWarnings("unchecked") Collection<?> collection =
                        (Collection<?>) iptcValue;

                    if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
                        for (Object o : collection) {
                            setValue(xmpColumn, o);
                        }
                    } else if (!collection.isEmpty()) {
                        boolean isSet =
                            options.equals(SetIptc.REPLACE_EXISTING_VALUES)
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
                    AppLogger.logWarning(Xmp.class, "Xmp.Error.SetIptc",
                                         iptcValue, xmpColumn);
                }
            }
        }
    }

    private String formatIptcDate(Column xmpColumn, String iptcString) {
        if (iptcString == null) {
            return null;
        }

        if (xmpColumn.equals(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)
                && (iptcString.length() == 8)) {
            if (iptcString.contains("-")) {
                return iptcString;
            }

            return iptcString.substring(0, 4) + "-"
                   + iptcString.substring(4, 6) + "-" + iptcString.substring(6);
        }

        return iptcString;
    }

    public Object getValue(Column xmpColumn) {
        Object o = valueOfColumn.get(xmpColumn);

        return (o instanceof List<?>)
               ? new ArrayList<Object>((List<?>) o)    // Returning a copy
               : o;
    }

    public void setValue(Column xmpColumn, Object value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            if (value != null) {
                addToCollection(xmpColumn, value);
            }
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
        Object  o      = valueOfColumn.get(xmpColumn);
        boolean remove = true;

        if (o instanceof Collection<?>) {
            @SuppressWarnings("unchecked") Collection<?> collection =
                (Collection<?>) o;

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
        if (value == null) {
            return;
        }

        Collection<? super Object> collection = collectionReferenceOf(column);

        if (collection == null) {
            collection = new ArrayList<Object>();
            valueOfColumn.put(column, collection);
        }

        if (!collection.contains(value)) {
            collection.add(value);
        }
    }

    public void set(Xmp xmp) {
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
