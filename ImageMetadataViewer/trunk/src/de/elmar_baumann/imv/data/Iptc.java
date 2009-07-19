package de.elmar_baumann.imv.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.database.metadata.mapping.IptcRepeatableValues;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IPTC metadata of an image file. The documentation of the mehtods contains
 * a link to the corresponding {@link Xmp} method in the <code>see</code>
 * section.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Iptc {

    private final Map<IPTCEntryMeta, Object> valueOfEntryMeta =
            new HashMap<IPTCEntryMeta, Object>();

    /**
     * Returns the IPTC fields 2:85 (By-line Title).
     * 
     * @return IPTC fields 2:85 or null if not defined
     * @see    Xmp#getPhotoshopAuthorsposition()
     */
    public List<String> getByLinesTitles() {
        return stringListOf(IPTCEntryMeta.BYLINE_TITLE);
    }

    /**
     * Adds a value to the IPTC field 2:85 (By-line Title).
     * 
     * @param byLineTitle IPTC field 2:85 (By-line Title)
     * @see               Xmp#setPhotoshopAuthorsposition(java.lang.String)
     */
    public void addByLineTitle(String byLineTitle) {
        addToStringList(IPTCEntryMeta.BYLINE_TITLE, byLineTitle);
    }

    /**
     * Returns the IPTC fields 2:80 (Byline).
     * 
     * @return IPTC fields 2:80 (Byline) or null if not defined
     * @see    Xmp#getDcCreator()
     */
    public List<String> getByLines() {
        return stringListOf(IPTCEntryMeta.BYLINE);
    }

    /**
     * Adds a value to the IPTC field 2:80 (Byline).
     * 
     * @param byLine IPTC field 2:80 (Byline)
     * @see          Xmp#setDcCreator(java.lang.String)
     */
    public void addByLine(String byLine) {
        addToStringList(IPTCEntryMeta.BYLINE, byLine);
    }

    /**
     * Returns the IPTC field 2:120 (Caption/Abstract).
     * 
     * @return IPTC field 2:120 (Caption/Abstract) or null if not defined
     * @see    Xmp#getDcDescription()
     */
    public String getCaptionAbstract() {
        return stringValueOf(IPTCEntryMeta.CAPTION_ABSTRACT);
    }

    /**
     * Sets the IPTC field 2:120 (Caption/Abstract).
     * 
     * @param captionAbstract IPTC field 2:120 (Caption/Abstract)
     * @see                   Xmp#setDcDescription(java.lang.String)
     */
    public void setCaptionAbstract(String captionAbstract) {
        valueOfEntryMeta.put(IPTCEntryMeta.CAPTION_ABSTRACT, captionAbstract);
    }

    /**
     * Returns the IPTC field 2:15 (Category).
     * 
     * @return IPTC field 2:15 (Category) or null if not defined
     * @see    Xmp#getPhotoshopCategory()
     */
    public String getCategory() {
        return stringValueOf(IPTCEntryMeta.CATEGORY);
    }

    /**
     * Sets the IPTC field 2:15 (Category).
     * 
     * @param category IPTC field 2:15 (Category)
     * @see            Xmp#setPhotoshopCategory(java.lang.String)
     */
    public void setCategory(String category) {
        valueOfEntryMeta.put(IPTCEntryMeta.CATEGORY, category);
    }

    /**
     * Returns the IPTC field 2:90 (City).
     * 
     * @return IPTC field 2:90 (City) or null if not defined
     * @see    Xmp#getPhotoshopCity()
     */
    public String getCity() {
        return stringValueOf(IPTCEntryMeta.CITY);
    }

    /**
     * Sets the IPTC field 2:90 (City).
     * 
     * @param city IPTC field 2:90 (City)
     * @see        Xmp#setPhotoshopCity(java.lang.String)
     */
    public void setCity(String city) {
        valueOfEntryMeta.put(IPTCEntryMeta.CITY, city);
    }

    /**
     * Returns the IPTC fields 2:26 (Content Location Code).
     * 
     * @return IPTC fields 2:26 (Content Location Code) or null if not defined
     * @see    Xmp#getIptc4xmpcoreCountrycode()
     */
    public List<String> getContentLocationCodes() {
        return stringListOf(IPTCEntryMeta.CONTENT_LOCATION_CODE);
    }

    /**
     * Adds a value to the IPTC field 2:26 (Content Location Code).
     * 
     * @param contentLocationCode IPTC field 2:26 (Content Location Code)
     * @see                       Xmp#setIptc4xmpcoreCountrycode(java.lang.String)
     */
    public void addContentLocationCode(String contentLocationCode) {
        addToStringList(IPTCEntryMeta.CONTENT_LOCATION_CODE, contentLocationCode);
    }

    /**
     * Returns the IPTC fields 2:27 (Content Location Name).
     * 
     * @return IPTC fields 2:27 (Content Location Name) or null if not defined
     * @see    Xmp#getIptc4xmpcoreLocation()
     */
    public List<String> getContentLocationNames() {
        return stringListOf(IPTCEntryMeta.CONTENT_LOCATION_NAME);
    }

    /**
     * Fügt ein IPTC field 2:27 (Content Location Name) hinzu.
     * 
     * @param contentLocationName IPTC field 2:27 (Content Location Name)
     * @see                       Xmp#setIptc4xmpcoreLocation(java.lang.String)
     */
    public void addContentLocationName(String contentLocationName) {
        addToStringList(IPTCEntryMeta.CONTENT_LOCATION_NAME, contentLocationName);
    }

    /**
     * Returns the IPTC field 2:116 (Copyright Notice).
     * 
     * @return IPTC field 2:116 (Copyright Notice) or null if not defined
     * @see    Xmp#getDcRights()
     */
    public String getCopyrightNotice() {
        return stringValueOf(IPTCEntryMeta.COPYRIGHT_NOTICE);
    }

    /**
     * Sets the IPTC field 2:116 (Copyright Notice).
     * 
     * @param copyrightNotice IPTC field 2:116 (Copyright Notice)
     * @see                   Xmp#setDcRights(java.lang.String)
     */
    public void setCopyrightNotice(String copyrightNotice) {
        valueOfEntryMeta.put(IPTCEntryMeta.COPYRIGHT_NOTICE, copyrightNotice);
    }

    /**
     * Returns the IPTC field 2:101 (Country/Primary Location Name).
     * 
     * @return IPTC field 2:101 (Country/Primary Location Name) or null if not
     *         defined
     * @see    Xmp#getPhotoshopCountry()
     */
    public String getCountryPrimaryLocationName() {
        return stringValueOf(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME);
    }

    /**
     * Sets the IPTC field 2:101 (Country/Primary Location Name).
     * 
     * @param countryPrimaryLocationName IPTC field 2:101 (Country/Primary
     *                                   Location Name)
     * @see                              Xmp#setPhotoshopCountry(java.lang.String)
     */
    public void setCountryPrimaryLocationName(String countryPrimaryLocationName) {
        valueOfEntryMeta.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME,
                countryPrimaryLocationName);
    }

    /**
     * Returns the IPTC field 2:110 (Credit).
     * 
     * @return IPTC field 2:110 (Credit) or null if not defined
     * @see    Xmp#getPhotoshopCredit()
     */
    public String getCredit() {
        return stringValueOf(IPTCEntryMeta.CREDIT);
    }

    /**
     * Sets the IPTC field 2:110 (Credit).
     * 
     * @param credit IPTC field 2:110 (Credit)
     * @see          Xmp#setPhotoshopCredit(java.lang.String)
     */
    public void setCredit(String credit) {
        valueOfEntryMeta.put(IPTCEntryMeta.CREDIT, credit);
    }

    /**
     * Returns the IPTC field 2:105 (Headline).
     * 
     * @return IPTC field 2:105 (Headline) or null if not defined
     * @see    Xmp#getPhotoshopHeadline()
     */
    public String getHeadline() {
        return stringValueOf(IPTCEntryMeta.HEADLINE);
    }

    /**
     * Sets the IPTC field 2:105 (Headline).
     * 
     * @param headline IPTC field 2:105 (Headline)
     * @see            Xmp#setPhotoshopHeadline(java.lang.String)
     */
    public void setHeadline(String headline) {
        valueOfEntryMeta.put(IPTCEntryMeta.HEADLINE, headline);
    }

    /**
     * Returns the IPTC fields 2:25 (Keywords).
     * 
     * @return IPTC fields 2:25 (Keywords) or null if not defined
     * @see    Xmp#getDcSubjects()
     */
    public List<String> getKeywords() {
        return stringListOf(IPTCEntryMeta.KEYWORDS);
    }

    /**
     * Fügt ein IPTC field 2:25 (Keywords) hinzu.
     * 
     * @param keyword IPTC field 2:25 (Keyword)
     * @see           Xmp#addDcSubject(java.lang.String)
     */
    public void addKeyword(String keyword) {
        addToStringList(IPTCEntryMeta.KEYWORDS, keyword);
    }

    /**
     * Returns the IPTC field 2:05 (Object Name).
     * 
     * @return IPTC field 2:05 (Object Name) or null if not defined
     * @see    Xmp#getDcTitle()
     */
    public String getObjectName() {
        return stringValueOf(IPTCEntryMeta.OBJECT_NAME);
    }

    /**
     * Sets the IPTC field 2:05 (Object Name).
     * 
     * @param objectName IPTC field 2:05 (Object Name)
     * @see              Xmp#setDcTitle(java.lang.String)
     */
    public void setObjectName(String objectName) {
        valueOfEntryMeta.put(IPTCEntryMeta.OBJECT_NAME, objectName);
    }

    /**
     * Returns the IPTC field 2:103 (Original Transmission Reference).
     * 
     * @return IPTC field 2:103 (Original Transmission Reference) or null if not
     *         defined
     * @see    Xmp#getPhotoshopTransmissionReference()
     */
    public String getOriginalTransmissionReference() {
        return stringValueOf(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE);
    }

    /**
     * Sets the IPTC field 2:103 (Original Transmission Reference).
     * 
     * @param originalTransmissionReference IPTC field 2:103 (Original
     *                                      Transmission Reference)
     * @see                                 Xmp#setPhotoshopTransmissionReference(java.lang.String)
     */
    public void setOriginalTransmissionReference(
            String originalTransmissionReference) {
        valueOfEntryMeta.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE,
                originalTransmissionReference);
    }

    /**
     * Returns the IPTC field 2:95 (Province/State).
     * 
     * @return IPTC field 2:95 (Province/State) or null if not defined
     * @see    Xmp#getPhotoshopState()
     */
    public String getProvinceState() {
        return stringValueOf(IPTCEntryMeta.PROVINCE_STATE);
    }

    /**
     * Sets the IPTC field 2:95 (Province/State).
     * 
     * @param provinceState IPTC field 2:95 (Province/State)
     * @see                 Xmp#setPhotoshopState(java.lang.String)
     */
    public void setProvinceState(String provinceState) {
        valueOfEntryMeta.put(IPTCEntryMeta.PROVINCE_STATE, provinceState);
    }

    /**
     * Returns the IPTC field 2:115 (Source).
     * 
     * @return IPTC field 2:115 (Source) or null if not defined
     * @see    Xmp#getPhotoshopSource()
     */
    public String getSource() {
        return stringValueOf(IPTCEntryMeta.SOURCE);
    }

    /**
     * Sets the IPTC field 2:115 (Source).
     * 
     * @param source IPTC field 2:115 (Source)
     * @see          Xmp#setPhotoshopSource(java.lang.String)
     */
    public void setSource(String source) {
        valueOfEntryMeta.put(IPTCEntryMeta.SOURCE, source);
    }

    /**
     * Returns the IPTC field 2:40 (Special Instructions).
     * 
     * @return ITPTC-Feld 2:40 (Special Instructions) or null if not defined
     * @see    Xmp#getPhotoshopInstructions()
     */
    public String getSpecialInstructions() {
        return stringValueOf(IPTCEntryMeta.SPECIAL_INSTRUCTIONS);
    }

    /**
     * Sets the IPTC field 2:40 (Special Instructions).
     * 
     * @param specialInstructions IPTC field 2:40 (Special Instructions)
     * @see                       Xmp#setPhotoshopInstructions(java.lang.String)
     */
    public void setSpecialInstructions(String specialInstructions) {
        valueOfEntryMeta.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS,
                specialInstructions);
    }

    /**
     * Returns the IPTC fields 2:20 (Supplemental Category).
     * 
     * @return IPTC fields 2:20 (Supplemental Category) or null if not defined
     * @see    Xmp#getPhotoshopSupplementalCategories()
     */
    public List<String> getSupplementalCategories() {
        return stringListOf(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY);
    }

    /**
     * Adds a value to the IPTC field 2:20 (Supplemental Category).
     * 
     * @param supplementalCategory IPTC field 2:20 (Supplemental Category)
     * @see                        Xmp#addPhotoshopSupplementalCategory(java.lang.String)
     */
    public void addSupplementalCategory(String supplementalCategory) {
        addToStringList(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY,
                supplementalCategory);
    }

    /**
     * Returns the IPTC fields 2:122 (Writer/Editor).
     * 
     * @return IPTC fields 2:122 (Writer/Editor) or null if not defined
     * @see    Xmp#getPhotoshopCaptionwriter()
     */
    public List<String> getWritersEditors() {
        return stringListOf(IPTCEntryMeta.WRITER_EDITOR);
    }

    /**
     * Adds a value to the IPTC field 2:122 (Writer/Editor).
     * 
     * @param writerEditor IPTC field 2:122 (Writer/Editor)
     * @see                Xmp#setPhotoshopCaptionwriter(java.lang.String)
     */
    public void addWriterEditor(String writerEditor) {
        addToStringList(IPTCEntryMeta.WRITER_EDITOR, writerEditor);
    }

    /**
     * Returns the value of an IPTC entry.
     * 
     * @param  iptcEntry  IPTC entry
     * @return value. It's a string for not repeatable values or a string list
     *         for repeatable values or null if this entry has no value
     */
    public Object getValue(IPTCEntryMeta iptcEntry) {
        return valueOfEntryMeta.get(iptcEntry);
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

    /**
     * Returns whether no field in this class was set.
     *
     * @return true if no field was set
     */
    public boolean isEmpty() {
        for (IPTCEntryMeta meta : valueOfEntryMeta.keySet()) {
            Object o = valueOfEntryMeta.get(meta);
            if (o instanceof String) {
                String string = (String) o;
                if (!string.trim().isEmpty()) return false;
            } else if (o instanceof List) {
                List list = (List) o;
                if (!list.isEmpty()) return false;
            // zuletzt, da leere Liste != null ist, aber trotzdem ein leeres Element
            } else if (o != null) {
                return false;
            }
        }
        return true;
    }

    private String stringValueOf(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof String
               ? (String) o
               : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> stringListOf(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof List
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
