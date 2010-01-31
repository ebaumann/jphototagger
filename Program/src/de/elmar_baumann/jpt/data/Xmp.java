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
import de.elmar_baumann.jpt.database.metadata.keywords.ColumnKeyword;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpRepeatableValues;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmp;
import de.elmar_baumann.jpt.event.listener.TextEntryListener;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * XMP metadata of an image file. The <code>see</code> sections of the method
 * documentation links to the corresponding {@link Iptc} method.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-22
 */
public final class Xmp implements TextEntryListener {

    private final Map<Column, Object> valueOfColumn = new HashMap<Column, Object>();
    private       Long                lastModified;

    /**
     * Delimiter of hierarchical subjects
     */
    public static final String HIER_SUBJECTS_DELIM = "|";

    public Xmp() {
    }

    public Xmp(Xmp other) {
        set(other);
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
        if (oldText != null && XmpRepeatableValues.isRepeatable(column)) {
            removeValue(column, oldText);
        }
        setValue(column, newText);
    }

    public void setMetaDataTemplate(MetadataTemplate template) {
        for (Column column : TableXmp.INSTANCE.getColumns()) {
            if (!column.isPrimaryKey() && !column.isForeignKey()) {
                setValue(column, template.getValueOfColumn(column));
            }
        }
    }

    public Set<String> getSubjects() {
        Collection<?> coll = collectionReferenceOf(ColumnKeyword.INSTANCE);
        if (coll == null) return null;
        Set<String> subjects = new HashSet<String>();

        for (Object o : coll) {
            if (o instanceof String) {
                subjects.addAll(KeywordsHelper.getHierarchicalSubjectsFromString((String) o));
            }
        }

        return subjects;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getLastModified() {
        return lastModified;
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

    /**
     * Sets all values of an {@link Iptc} instance.
     * @param iptc    IPTC instance
     * @param options options (how to set)
     */
    public void setIptc(Iptc iptc, SetIptc options) {
        if (options.equals(SetIptc.REPLACE_EXISTING_VALUES)) {
            empty();
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
                    List<String> array = (List<String>) iptcValue;
                    if (xmpColumn.equals(ColumnKeyword.INSTANCE)) {
                        for (String string : array) {
                            setValue(ColumnKeyword.INSTANCE, string);
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

    public Long getRating() {
        Object o = getValue(ColumnXmpRating.INSTANCE);
        if (o instanceof Long) {
            long rating = (Long) o;
            return rating < ColumnXmpRating.getMinValue()
                    ? null
                    : rating > ColumnXmpRating.getMaxValue()
                    ? ColumnXmpRating.getMaxValue()
                    : rating;
        }
        return null;
    }

    public void setRating(Long rating) {
        if (rating == null || rating < ColumnXmpRating.getMinValue()) {
            valueOfColumn.remove(ColumnXmpRating.INSTANCE);
            return;
        }
        valueOfColumn.put(ColumnXmpRating.INSTANCE,
                          rating > ColumnXmpRating.getMaxValue()
                                ? ColumnXmpRating.getMaxValue()
                                : rating);
    }

    /**
     * Returns a value of a XMP column.
     *
     * @param  xmpColumn  XMP column
     * @return Value or null if not set. Current values:
     *         <ul>
     *         <li>String when set with a <code>set...()</code> method setting
     *             a string
     *         <li>A {@link Collection} if
     *             {@link XmpRepeatableValues#isRepeatable(de.elmar_baumann.jpt.database.metadata.Column)}
     *             returns true
     *         <li>Long when set with a <code>set...()</code> method setting a
     *             Long value (lastmodified)
     */
    @SuppressWarnings("unchecked")
    public Object getValue(Column xmpColumn) {
        Object o = valueOfColumn.get(xmpColumn);
        return o instanceof Collection<?>
                 ? deepCopy((Collection<?>) o)
                 : o;
    }

    /**
     * Sets a value of a column.
     *
     * @param xmpColumn XMP column
     * @param value     value
     */
    @SuppressWarnings("unchecked")
    public void setValue(Column xmpColumn, Object value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            Object o = valueOfColumn.get(xmpColumn);
            if (o == null) {
                valueOfColumn.put(xmpColumn, new ArrayList<Object>(Arrays.asList(value)));
            } else if (o instanceof Collection<?>) {
                ((Collection<Object>) o).add(value);
            }  else {
                assert false : "Can't handle " + value + " of column " + xmpColumn;
            }
        } else {
            valueOfColumn.put(xmpColumn, value);
        }
    }

    /**
     * Removes a value of a XMP column.
     *
     * @param xmpColumn  XMP column
     * @param value      value not null
     */
    @SuppressWarnings({"unchecked", "element-type-mismatch"})
    public void removeValue(Column xmpColumn, String value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            if (value == null) {
                valueOfColumn.remove(xmpColumn);
                return;
            }
            Object o = valueOfColumn.get(xmpColumn);
            assert o == null || o instanceof Collection<?> : o;
            if (o instanceof Collection<?>) {
                Collection<?> coll = (Collection<?>) o;
                coll.remove(value);
                if (coll.isEmpty()) {
                    valueOfColumn.remove(xmpColumn);
                }
            }
        } else {
            valueOfColumn.remove(xmpColumn);
        }
    }

    /**
     * Removes all XMP values.
     */
    public void empty() {
        valueOfColumn.clear();
    }

    /**
     * Returns, whether no value was set.
     *
     * @return true if no values were set.
     */
    public boolean isEmpty() {
        for (Column column : valueOfColumn.keySet()) {
            Object o = valueOfColumn.get(column);
            if (o instanceof String) {
                String string = (String) o;
                if (!string.trim().isEmpty()) return false;
            } else if (o instanceof Collection<?>) {
                return ((Collection<?>)o).size() > 0;
            } else if (o != null) {
                return false;
            }
        }
        return true;
    }

    private Collection<?> collectionReferenceOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof Collection<?>
                ? (Collection<?>)o
                : null;
    }

    private Collection<?> deepCopy(Collection<?> coll) {
        return new ArrayList<Object>(coll);
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
            setValue(column, xmp.valueOfColumn.get(column));
        }
    }

    @Override
    public String toString() {
        return valueOfColumn.toString();
    }
}
