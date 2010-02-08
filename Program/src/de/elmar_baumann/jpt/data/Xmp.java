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
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Table;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpRepeatableValues;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.XmpTables;
import de.elmar_baumann.jpt.event.listener.TextEntryListener;
import de.elmar_baumann.lib.generics.Pair;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * XMP metadata of an image file. The <code>see</code> sections of the method
 * documentation links to the corresponding {@link Iptc} method.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-22
 */
public final class Xmp implements TextEntryListener {

    private final Map<Column, Object> valueOfColumn = new HashMap<Column, Object>();

    public Xmp() {
    }

    public Xmp(Xmp other) {
        set(other);
    }

    public boolean contains(Column column) {
        return valueOfColumn.get(column) != null;
    }

    public Object remove(Column column) {
        return valueOfColumn.remove(column);
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
    @SuppressWarnings("unchecked")
    public void textChanged(Column column, String oldText, String newText) {
        if (XmpRepeatableValues.isRepeatable(column)) {
            Object o = valueOfColumn.get(column);
            if (o == null) {
                List<Object> list = new ArrayList<Object>();
                list.add(newText);
                valueOfColumn.put(column, list);
            }
            assert o instanceof List<?> : "Not a List: " + o;
            if (o instanceof List<?>) {
                List<Object> list = (List<Object>) o;
                int index = list.indexOf(oldText);
                if (index >= 0) {
                    list.set(index, newText);
                } else {
                    list.add(newText);
                }
            }
        } else {
            setValue(column, newText);
        }
    }

    public void setMetaDataTemplate(MetadataTemplate template) {
        for (Table xmpTable : XmpTables.get()) {
            for (Column column : xmpTable.getColumns()) {
                if (!column.isPrimaryKey() && !column.isForeignKey()) {
                    valueOfColumn.put(column, template.getValueOfColumn(column));
                }
            }
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
    };

    public void setIptc(Iptc iptc, SetIptc options) {
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
                    boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES) || getValue(xmpColumn) == null;
                    if (isSet) {
                        iptcString = formatIptcDate(xmpColumn, iptcString);
                        setValue(xmpColumn, iptcString);
                    }
                } else if (iptcValue instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) iptcValue;
                    if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
                        for (Object o : list) {
                            setValue(xmpColumn, o);
                        }
                    } else {
                        if (!list.isEmpty()) {
                            setValue(xmpColumn, list.get(0));
                        }
                    }
                } else {
                    AppLogger.logWarning(Xmp.class, "Xmp.Error.SetIptc", iptcValue, xmpColumn);
                }
            }
        }
    }

    private String formatIptcDate(Column xmpColumn, String iptcString) {
        if (iptcString == null) return null;
        if (xmpColumn.equals(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE) && iptcString.length() == 8) {
            if (iptcString.contains("-")) return iptcString;
            return        iptcString.substring(0, 4) +
                    "-" + iptcString.substring(4, 6) +
                    "-" + iptcString.substring(6);
        }
        return iptcString;
    }

    public Object getValue(Column xmpColumn) {
        Object o = valueOfColumn.get(xmpColumn);
        return o instanceof List<?>
               ? new ArrayList<Object>((List<?>) o)
               : o;
    }

    public void setValue(Column xmpColumn, Object value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            if (value != null) {
                addToList(xmpColumn, value);
            }
        } else {
            valueOfColumn.put(xmpColumn, value);
        }
    }

    /**
     * Removes a value of a XMP column.
     *
     * @param xmpColumn  XMP column
     * @param value      value not null. If the column contains a repeatable
     *                   value it will be removed from it's list.
     */
    public void removeValue(Column xmpColumn, Object value) {
        Object o = valueOfColumn.get(xmpColumn);
        boolean remove = true;
        if (o instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) o;
            list.remove(value);
            remove = list.isEmpty();
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
                if (!string.trim().isEmpty()) return false;
            } else if (o instanceof List<?>) {
                List<?> list = (List<?>) o;
                if (!list.isEmpty()) return false;
            } else if (o != null) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private List<Object> listReferenceOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof List<?>
               ? (List<Object>) o
               : null;
    }

    private void addToList(Column column, Object value) {
        if (value == null) return;
        List<Object> list = listReferenceOf(column);
        if (list == null) {
            list = new ArrayList<Object>();
            valueOfColumn.put(column, list);
        }
        if (!list.contains(value)) {
            list.add(value);
        }
    }

    /**
     * Sets the values of an other XMP object via deep copy.
     *
     * @param xmp Other XMP object
     */
    // All get() calls are returning null after clear(). Thus a column with a
    // value of null has not to be set.
    public void set(Xmp xmp) {
        if (xmp == this) return;
        valueOfColumn.clear();
        for (Column column : xmp.valueOfColumn.keySet()) {
            Object o = xmp.valueOfColumn.get(column);
            if (o instanceof List<?>) {
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
