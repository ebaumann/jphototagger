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
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
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

    private final Map<Column, Object> valueOfColumn       = new HashMap<Column, Object>();
    private       String              hierarchicalSubjects;

    /**
     * Delimiter of hierarchical subjects
     */
    public static final String        HIER_SUBJECTS_DELIM = "|";

    public Xmp() {
    }

    public Xmp(Xmp other) {
        set(other);
    }

    /**
     * Returns the XMP value of dc:creator (photographer).
     *
     * @return value of dc:creator (photographer) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getByLines()
     */
    public String getDcCreator() {
        return stringValueOf(ColumnXmpDcCreator.INSTANCE);
    }

    /**
     * Sets the value of dc:creator (photographer).
     *
     * @param creator value of dc:creator (photographer) not null
     * @see           de.elmar_baumann.jpt.data.Iptc#addByLine(java.lang.String)
     */
    public void setDcCreator(String creator) {
        valueOfColumn.put(ColumnXmpDcCreator.INSTANCE, creator);
    }

    /**
     * Returns the XMP value of dc:description (image description).
     *
     * @return value of dc:description (image description) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getCaptionAbstract()
     */
    public String getDcDescription() {
        return stringValueOf(ColumnXmpDcDescription.INSTANCE);
    }

    /**
     * Sets the value of dc:description (image description).
     *
     * @param dcDescription value of dc:description (image description)
     * @see                 de.elmar_baumann.jpt.data.Iptc#setCaptionAbstract(java.lang.String)
     */
    public void setDcDescription(String dcDescription) {
        valueOfColumn.put(ColumnXmpDcDescription.INSTANCE, dcDescription);
    }

    /**
     * Returns the XMP value of dc:rights (Copyright).
     *
     * @return value of dc:rights (Copyright) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getCopyrightNotice()
     */
    public String getDcRights() {
        return stringValueOf(ColumnXmpDcRights.INSTANCE);
    }

    /**
     * Sets the value of dc:rights (Copyright).
     *
     * @param dcRights value of dc:rights (Copyright)
     * @see            de.elmar_baumann.jpt.data.Iptc#setCopyrightNotice(java.lang.String)
     */
    public void setDcRights(String dcRights) {
        valueOfColumn.put(ColumnXmpDcRights.INSTANCE, dcRights);
    }

    /**
     * Returns the values of dc:subject (keywords, tags).
     *
     * @return value of dc:subject (keywords, tags) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getKeywords()
     */
    public List<String> getDcSubjects() {
        List<String> list = stringListReferenceOf(ColumnXmpDcSubjectsSubject.INSTANCE);
        return list == null
               ? null
               : new ArrayList<String>(list);
    }

    public void setDcSubjects(List<String> subjects) {
        List<String> list = stringListReferenceOf(ColumnXmpDcSubjectsSubject.INSTANCE);
        if (list == null) {
            valueOfColumn.put(ColumnXmpDcSubjectsSubject.INSTANCE, new ArrayList<String>(subjects));
        } else {
            list.clear();
            list.addAll(subjects);
        }
    }

    /**
     * Adds a value to dc:subject (keyword, tag).
     *
     * @param subject value of dc:subject (keyword, tag) not null
     * @see           de.elmar_baumann.jpt.data.Iptc#addKeyword(java.lang.String)
     */
    public void addDcSubject(String subject) {
        addToStringList(ColumnXmpDcSubjectsSubject.INSTANCE, subject);
    }

    /**
     * Returns the XMP value of dc:title (image title).
     *
     * @return value of dc:title (image title) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getObjectName()
     */
    public String getDcTitle() {
        return stringValueOf(ColumnXmpDcTitle.INSTANCE);
    }

    /**
     * Sets the value of dc:title (image title).
     *
     * @param dcTitle value of dc:title (image title)
     * @see           de.elmar_baumann.jpt.data.Iptc#setObjectName(java.lang.String)
     */
    public void setDcTitle(String dcTitle) {
        valueOfColumn.put(ColumnXmpDcTitle.INSTANCE, dcTitle);
    }

    /**
     * Returns the XMP value of Iptc4xmpCore:CountryCode (ISO country code).
     *
     * @return value of Iptc4xmpCore:CountryCode (ISO country code) or null if not
     *         set
     * @see             de.elmar_baumann.jpt.data.Iptc#getContentLocationCodes()
     */
    public String getIptc4XmpCoreCountrycode() {
        return stringValueOf(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
    }

    /**
     * Sets the value of Iptc4xmpCore:CountryCode (ISO country code).
     *
     * @param iptc4xmpcoreCountrycode value of Iptc4xmpCore:CountryCode (ISO country code)
     */
    public void setIptc4XmpCoreCountrycode(String iptc4xmpcoreCountrycode) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, iptc4xmpcoreCountrycode);
    }

    /**
     * Returns the XMP value of Iptc4xmpCore:Location (location where the image
     * was taken).
     *
     * @return value of Iptc4xmpCore:Location or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getContentLocationNames()
     */
    public String getIptc4XmpCoreLocation() {
        return stringValueOf(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
    }

    /**
     * Sets the value of Iptc4xmpCore:Location (location where the image
     * was taken).
     *
     * @param iptc4xmpcoreLocation value of Iptc4xmpCore:Location
     */
    public void setIptc4XmpCoreLocation(String iptc4xmpcoreLocation) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, iptc4xmpcoreLocation);
    }

    public void setIptc4XmpCoreDateCreated(String date) {
        valueOfColumn.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, date);
    }

    public String getIptc4XmpCoreDateCreated() {
        return stringValueOf(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
    }

    /**
     * Liefert value of photoshop:AuthorsPosition (position of the photographer).
     *
     * @return value of photoshop:AuthorsPosition (position of the photographer)
     *         or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getByLinesTitles()
     */
    public String getPhotoshopAuthorsposition() {
        return stringValueOf(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
    }

    /**
     * Sets the value of photoshop:AuthorsPosition (position of the photographer).
     *
     * @param photoshopAuthorsposition value of photoshop:AuthorsPosition
     *        (position of the photographer)
     */
    public void setPhotoshopAuthorsposition(String photoshopAuthorsposition) {
        valueOfColumn.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, photoshopAuthorsposition);
    }

    /**
     * Returns the XMP value of photoshop:CaptionWriter (author of the
     * description).
     *
     * @return value of photoshop:CaptionWriter (author of the description) or
     *         null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getWritersEditors()
     */
    public String getPhotoshopCaptionwriter() {
        return stringValueOf(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
    }

    /**
     * Sets the value of photoshop:CaptionWriter (author of the description).
     *
     * @param photoshopCaptionwriter value of photoshop:CaptionWriter (author of
     *                               the description)
     */
    public void setPhotoshopCaptionwriter(String photoshopCaptionwriter) {
        valueOfColumn.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, photoshopCaptionwriter);
    }

    /**
     * Returns the XMP value of photoshop:City (city of the photographer).
     *
     * @return value of photoshop:City (city of the photographer) or null if not
     *         set
     * @see    de.elmar_baumann.jpt.data.Iptc#getCity()
     */
    public String getPhotoshopCity() {
        return stringValueOf(ColumnXmpPhotoshopCity.INSTANCE);
    }

    /**
     * Sets the value of photoshop:City (city of the photographer).
     *
     * @param photoshopCity value of photoshop:City (city of the photographer)
     * @see                 de.elmar_baumann.jpt.data.Iptc#setCity(java.lang.String)
     */
    public void setPhotoshopCity(String photoshopCity) {
        valueOfColumn.put(ColumnXmpPhotoshopCity.INSTANCE, photoshopCity);
    }

    /**
     * Returns the XMP value of photoshop:Country (country of the photographer).
     *
     * @return value of photoshop:Country (country of the photographer) or null
     *         if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getCountryPrimaryLocationName()
     */
    public String getPhotoshopCountry() {
        return stringValueOf(ColumnXmpPhotoshopCountry.INSTANCE);
    }

    /**
     * Sets the value of photoshop:Country (country of the photographer).
     *
     * @param photoshopCountry value of photoshop:Country (country of the
     *                         photographer)
     * @see   de.elmar_baumann.jpt.data.Iptc#setCountryPrimaryLocationName(java.lang.String)
     */
    public void setPhotoshopCountry(String photoshopCountry) {
        valueOfColumn.put(ColumnXmpPhotoshopCountry.INSTANCE, photoshopCountry);
    }

    /**
     * Returns the XMP value of photoshop:Credit (provider of the image).
     *
     * @return value of photoshop:Credit (provider of the image) or null if not
     *         set
     * @see    de.elmar_baumann.jpt.data.Iptc#getCredit()
     */
    public String getPhotoshopCredit() {
        return stringValueOf(ColumnXmpPhotoshopCredit.INSTANCE);
    }

    /**
     * Sets the value of photoshop:Credit (provider of the image).
     *
     * @param photoshopCredit value of photoshop:Credit (provider of the image)
     * @see   de.elmar_baumann.jpt.data.Iptc#setCredit(java.lang.String)
     */
    public void setPhotoshopCredit(String photoshopCredit) {
        valueOfColumn.put(ColumnXmpPhotoshopCredit.INSTANCE, photoshopCredit);
    }

    /**
     * Returns the XMP value of photoshop:Headline (image title).
     *
     * @return value of photoshop:Headline (image title) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getHeadline()
     */
    public String getPhotoshopHeadline() {
        return stringValueOf(ColumnXmpPhotoshopHeadline.INSTANCE);
    }

    /**
     * Sets the value of photoshop:Headline (image title).
     *
     * @param photoshopHeadline value of photoshop:Headline (image title)
     * @see                     de.elmar_baumann.jpt.data.Iptc#setHeadline(java.lang.String)
     */
    public void setPhotoshopHeadline(String photoshopHeadline) {
        valueOfColumn.put(ColumnXmpPhotoshopHeadline.INSTANCE, photoshopHeadline);
    }

    /**
     * Returns the XMP value of photoshop:Instructions (instructions).
     *
     * @return value of photoshop:Instructions (instructions) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getSpecialInstructions()
     */
    public String getPhotoshopInstructions() {
        return stringValueOf(ColumnXmpPhotoshopInstructions.INSTANCE);
    }

    /**
     * Sets the value of photoshop:Instructions (instructions).
     *
     * @param photoshopInstructions value of photoshop:Instructions
     *                              (instructions)
     * @see                         de.elmar_baumann.jpt.data.Iptc#setSpecialInstructions(java.lang.String)
     */
    public void setPhotoshopInstructions(String photoshopInstructions) {
        valueOfColumn.put(ColumnXmpPhotoshopInstructions.INSTANCE, photoshopInstructions);
    }

    /**
     * Returns the XMP value of photoshop:Source (image source).
     *
     * @return value of photoshop:Source (image source) or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getSource()
     */
    public String getPhotoshopSource() {
        return stringValueOf(ColumnXmpPhotoshopSource.INSTANCE);
    }

    /**
     * Sets the value of photoshop:Source (image source).
     *
     * @param photoshopSource value of photoshop:Source (image source)
     * @see                   de.elmar_baumann.jpt.data.Iptc#setSource(java.lang.String)
     */
    public void setPhotoshopSource(String photoshopSource) {
        valueOfColumn.put(ColumnXmpPhotoshopSource.INSTANCE, photoshopSource);
    }

    /**
     * Returns the XMP value of photoshop:State (state of the photographer).
     *
     * @return value of photoshop:State (state of the photographer) or null if
     *         not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getProvinceState()
     */
    public String getPhotoshopState() {
        return stringValueOf(ColumnXmpPhotoshopState.INSTANCE);
    }

    /**
     * Sets the value of photoshop:State (state of the photographer).
     *
     * @param photoshopState value of photoshop:State (state of the photographer)
     * @see                  de.elmar_baumann.jpt.data.Iptc#setProvinceState(java.lang.String)
     */
    public void setPhotoshopState(String photoshopState) {
        valueOfColumn.put(ColumnXmpPhotoshopState.INSTANCE, photoshopState);
    }

    /**
     * Returns the XMP value of photoshop:TransmissionReference.
     *
     * @return value of photoshop:TransmissionReference or null if not set
     * @see    de.elmar_baumann.jpt.data.Iptc#getOriginalTransmissionReference()
     */
    public String getPhotoshopTransmissionReference() {
        return stringValueOf(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
    }

    /**
     * Sets the value of photoshop:TransmissionReference.
     *
     * @param photoshopTransmissionReference value of
     *                                       photoshop:TransmissionReference
     * @see                                  de.elmar_baumann.jpt.data.Iptc#setOriginalTransmissionReference(java.lang.String)
     */
    public void setPhotoshopTransmissionReference(String photoshopTransmissionReference) {
        valueOfColumn.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, photoshopTransmissionReference);
    }

    /**
     * Returns the XMP rating.
     *
     * @return value of rating or null if not set
     */
    public Long getRating() {
        return longValueOf(ColumnXmpRating.INSTANCE);
    }

    /**
     * Sets the XMP rating.
     *
     * @param rating value of rating not null
     */
    public void setRating(Long rating) {
        valueOfColumn.put(ColumnXmpRating.INSTANCE, rating);
    }

    /**
     * Sets the last modification time of the XMP data.
     *
     * @param lastModified  milliseconds since 1970 of the modification time
     */
    public void setLastModified(long lastModified) {
        valueOfColumn.put(ColumnXmpLastModified.INSTANCE, lastModified);
    }

    /**
     * Returns the last modification time of the XMP data.
     *
     * @return milliseconds since 1970 of the modification time or null
     *         if not defined
     */
    public Long getLastModified() {
        return longValueOf(ColumnXmpLastModified.INSTANCE);
    }

    /**
     * Sets hierarchical subjects.
     *
     * @param subjects subjects. The first element is the to level element
     *                 (direct below the root), the second the 2nd level etc.
     */
    public void setHierarchicalSubjects(List<String> subjects) {
        StringBuilder sb    = new StringBuilder();
        int           size  = subjects.size();

        for (int i = 0; i < size; i++) {
            sb.append(subjects.get(i));
            sb.append(i == size - 1 ? "" : HIER_SUBJECTS_DELIM);
        }

        hierarchicalSubjects = sb.toString();
    }

    /**
     * Returns the hierarchical subjects delimited by {@link #HIER_SUBJECTS_DELIM}.
     *
     * @return hierarchical subjects
     */
    public String getHierarchicalSubjects() {
        return hierarchicalSubjects;
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
                List<String> list = new ArrayList<String>();
                list.add(newText);
                valueOfColumn.put(column, list);
            }
            assert o instanceof List<?> : "Not a List: " + o;
            if (o instanceof List<?>) {
                List<String> list = (List<String>) o;
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
                    if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
                        for (String string : array) {
                            setValue(xmpColumn, string);
                        }
                    } else {
                        if (!array.isEmpty()) {
                            setValue(xmpColumn, array.get(0));
                        }
                    }
                } else {
                    AppLogger.logWarning(
                            Xmp.class, "Xmp.Error.SetIptc", iptcValue, xmpColumn);
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

    /**
     * Returns a value of a XMP column.
     *
     * @param  xmpColumn  XMP column
     * @return Value or null if not set. Current values:
     *         <ul>
     *         <li>String when set with a <code>set...()</code> method setting
     *             a string
     *         <li>String list when set with a
     *             <code>add...()</code> methoden adding a string
     *         <li>Long when set with a <code>set...()</code> method setting a
     *             Long value (lastmodified)
     */
    @SuppressWarnings("unchecked")
    public Object getValue(Column xmpColumn) {
        Object o = valueOfColumn.get(xmpColumn);
        assert o == null || o instanceof List<?> || o instanceof String || o instanceof Long : "Neither List nor String nor Long: " + o;
        return o instanceof List<?>
               ? new ArrayList<String>((List<String>) o)
               : o instanceof String || o instanceof Long
                 ? o
                 : null;
    }

    /**
     * Sets a value of a column. When the column has repetable values it will
     * be added to it's list.
     *
     * @param xmpColumn  XMP column
     * @param value      value
     */
    public void setValue(Column xmpColumn, Object value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            if (value != null) {
                assert value instanceof String; // Other not recoginzed
                addToStringList(xmpColumn, value.toString());
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
    @SuppressWarnings("unchecked")
    public void removeValue(Column xmpColumn, String value) {
        Object o = valueOfColumn.get(xmpColumn);
        boolean remove = true;
        if (o instanceof List<?>) {
            List<String> list = (List<String>) o;
            list.remove(value);
            remove = list.isEmpty();
        }
        if (remove) {
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
            } else if (o instanceof List<?>) {
                List<?> list = (List<?>) o;
                if (!list.isEmpty()) return false;
            } else if (o != null) { // last position, empty list can be != null but empty
                return false;
            }
        }
        return true;
    }

    private Long longValueOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof Long
               ? (Long) o
               : null;
    }

    private String stringValueOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof String
               ? (String) o
               : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> stringListReferenceOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof List<?>
               ? (List<String>) o
               : null;
    }

    private void addToStringList(Column column, String string) {
        if (string == null) return;
        List<String> list = stringListReferenceOf(column);
        if (list == null) {
            list = new ArrayList<String>();
            valueOfColumn.put(column, list);
        }
        if (!list.contains(string)) {
            list.add(string);
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
            if (isImmutable(o)) {
                valueOfColumn.put(column, o);
            } else if (o instanceof List<?>) {
                valueOfColumn.put(column, deepCopy((List<?>) o));
            } else if (o != null) {
                assert false : "Unregognized data type of: " + o;
            }
        }
    }

    // If true o is not null
    private boolean isImmutable(Object o) {
        return o instanceof Boolean ||
                o instanceof Character ||
                o instanceof Byte ||
                o instanceof Short ||
                o instanceof Integer ||
                o instanceof Long ||
                o instanceof Float ||
                o instanceof Double ||
                o instanceof String;
    }

    private List<Object> deepCopy(List<?> list) {
        List<Object> copy = new ArrayList<Object>(list.size());
        for (Object o : list) {
            if (isImmutable(o)) {
                copy.add(o);
            } else { // Even null in a collection is not valid
                assert false : "Unregognized data type of: " + o;
            }
        }
        return copy;
    }

    @Override
    public String toString() {
        return valueOfColumn.toString();
    }
}
