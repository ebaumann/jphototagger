package de.elmar_baumann.imv.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.database.metadata.mapping.IptcRepeatableValues;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IPTC-Metadaten einer Bilddatei. Unter den Operationen ist ein Link auf die
 * Operationen von {@link Xmp}, die dort das gleiche bedeuten.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 * @see     Xmp
 */
public final class Iptc {

    private final Map<IPTCEntryMeta, Object> valueOfEntryMeta = new HashMap<IPTCEntryMeta, Object>();

    /**
     * Liefert die IPTC-Felder 2:85 (By-line Title).
     * 
     * @return IPTC-Felder 2:85 oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopAuthorsposition()
     */
    public List<String> getByLinesTitles() {
        return stringListOf(IPTCEntryMeta.BYLINE_TITLE);
    }

    /**
     * Fügt ein IPTC-Feld 2:85 (By-line Title) hinzu.
     * 
     * @param byLineTitle IPTC-Feld 2:85 (By-line Title)
     * @see               Xmp#setPhotoshopAuthorsposition(java.lang.String)
     */
    public void addByLineTitle(String byLineTitle) {
        addToStringList(IPTCEntryMeta.BYLINE_TITLE, byLineTitle);
    }

    /**
     * Liefert die IPTC-Felder 2:80 (Byline).
     * 
     * @return IPTC-Felder 2:80 (Byline) oder null, wenn nicht definiert
     * @see    Xmp#getDcCreator()
     */
    public List<String> getByLines() {
        return stringListOf(IPTCEntryMeta.BYLINE);
    }

    /**
     * Fügt ein IPTC-Feld 2:80 (Byline) hinzu.
     * 
     * @param byLine IPTC-Feld 2:80 (Byline)
     * @see          Xmp#setDcCreator(java.lang.String)
     */
    public void addByLine(String byLine) {
        addToStringList(IPTCEntryMeta.BYLINE, byLine);
    }

    /**
     * Liefert das IPTC-Feld 2:120 (Caption/Abstract).
     * 
     * @return IPTC-Feld 2:120 (Caption/Abstract) oder null, wenn nicht definiert
     * @see    Xmp#getDcDescription()
     */
    public String getCaptionAbstract() {
        return stringValueOf(IPTCEntryMeta.CAPTION_ABSTRACT);
    }

    /**
     * Setzt das IPTC-Feld 2:120 (Caption/Abstract).
     * 
     * @param captionAbstract IPTC-Feld 2:120 (Caption/Abstract)
     * @see                   Xmp#setDcDescription(java.lang.String)
     */
    public void setCaptionAbstract(String captionAbstract) {
        valueOfEntryMeta.put(IPTCEntryMeta.CAPTION_ABSTRACT, captionAbstract);
    }

    /**
     * Liefert das IPTC-Feld 2:15 (Category).
     * 
     * @return IPTC-Feld 2:15 (Category) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCategory()
     */
    public String getCategory() {
        return stringValueOf(IPTCEntryMeta.CATEGORY);
    }

    /**
     * Setzt das IPTC-Feld 2:15 (Category).
     * 
     * @param category IPTC-Feld 2:15 (Category)
     * @see            Xmp#setPhotoshopCategory(java.lang.String)
     */
    public void setCategory(String category) {
        valueOfEntryMeta.put(IPTCEntryMeta.CATEGORY, category);
    }

    /**
     * Liefert das IPTC-Feld 2:90 (City).
     * 
     * @return IPTC-Feld 2:90 (City) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCity()
     */
    public String getCity() {
        return stringValueOf(IPTCEntryMeta.CITY);
    }

    /**
     * Setzt das IPTC-Feld 2:90 (City).
     * 
     * @param city IPTC-Feld 2:90 (City)
     * @see        Xmp#setPhotoshopCity(java.lang.String)
     */
    public void setCity(String city) {
        valueOfEntryMeta.put(IPTCEntryMeta.CITY, city);
    }

    /**
     * Liefert die IPTC-Felder 2:26 (Content Location Code).
     * 
     * @return IPTC-Felder 2:26 (Content Location Code) oder null, wenn nicht definiert
     * @see    Xmp#getIptc4xmpcoreCountrycode()
     */
    public List<String> getContentLocationCodes() {
        return stringListOf(IPTCEntryMeta.CONTENT_LOCATION_CODE);
    }

    /**
     * Fügt ein IPTC-Feld 2:26 (Content Location Code) hinzu.
     * 
     * @param contentLocationCode IPTC-Feld 2:26 (Content Location Code)
     * @see                       Xmp#setIptc4xmpcoreCountrycode(java.lang.String)
     */
    public void addContentLocationCode(String contentLocationCode) {
        addToStringList(IPTCEntryMeta.CONTENT_LOCATION_CODE, contentLocationCode);
    }

    /**
     * Liefert die IPTC-Felder 2:27 (Content Location Name).
     * 
     * @return IPTC-Felder 2:27 (Content Location Name) oder null, wenn nicht definiert
     * @see    Xmp#getIptc4xmpcoreLocation()
     */
    public List<String> getContentLocationNames() {
        return stringListOf(IPTCEntryMeta.CONTENT_LOCATION_NAME);
    }

    /**
     * Fügt ein IPTC-Feld 2:27 (Content Location Name) hinzu.
     * 
     * @param contentLocationName IPTC-Feld 2:27 (Content Location Name)
     * @see                       Xmp#setIptc4xmpcoreLocation(java.lang.String)
     */
    public void addContentLocationName(String contentLocationName) {
        addToStringList(IPTCEntryMeta.CONTENT_LOCATION_NAME, contentLocationName);
    }

    /**
     * Liefert das IPTC-Feld 2:116 (Copyright Notice).
     * 
     * @return IPTC-Feld 2:116 (Copyright Notice) oder null, wenn nicht definiert
     * @see    Xmp#getDcRights()
     */
    public String getCopyrightNotice() {
        return stringValueOf(IPTCEntryMeta.COPYRIGHT_NOTICE);
    }

    /**
     * Setzt das IPTC-Feld 2:116 (Copyright Notice).
     * 
     * @param copyrightNotice IPTC-Feld 2:116 (Copyright Notice)
     * @see                   Xmp#setDcRights(java.lang.String)
     */
    public void setCopyrightNotice(String copyrightNotice) {
        valueOfEntryMeta.put(IPTCEntryMeta.COPYRIGHT_NOTICE, copyrightNotice);
    }

    /**
     * Liefert das IPTC-Feld 2:101 (Country/Primary Location Name).
     * 
     * @return IPTC-Feld 2:101 (Country/Primary Location Name) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCountry()
     */
    public String getCountryPrimaryLocationName() {
        return stringValueOf(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME);
    }

    /**
     * Setzt das IPTC-Feld 2:101 (Country/Primary Location Name).
     * 
     * @param countryPrimaryLocationName IPTC-Feld 2:101 (Country/Primary Location Name)
     * @see                              Xmp#setPhotoshopCountry(java.lang.String)
     */
    public void setCountryPrimaryLocationName(String countryPrimaryLocationName) {
        valueOfEntryMeta.put(IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME,
            countryPrimaryLocationName);
    }

    /**
     * Liefert das IPTC-Feld 2:110 (Credit).
     * 
     * @return IPTC-Feld 2:110 (Credit) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCredit()
     */
    public String getCredit() {
        return stringValueOf(IPTCEntryMeta.CREDIT);
    }

    /**
     * Setzt das IPTC-Feld 2:110 (Credit).
     * 
     * @param credit IPTC-Feld 2:110 (Credit)
     * @see          Xmp#setPhotoshopCredit(java.lang.String)
     */
    public void setCredit(String credit) {
        valueOfEntryMeta.put(IPTCEntryMeta.CREDIT, credit);
    }

    /**
     * Liefert das IPTC-Feld 2:105 (Headline).
     * 
     * @return IPTC-Feld 2:105 (Headline) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopHeadline()
     */
    public String getHeadline() {
        return stringValueOf(IPTCEntryMeta.HEADLINE);
    }

    /**
     * Setzt das IPTC-Feld 2:105 (Headline).
     * 
     * @param headline IPTC-Feld 2:105 (Headline)
     * @see            Xmp#setPhotoshopHeadline(java.lang.String)
     */
    public void setHeadline(String headline) {
        valueOfEntryMeta.put(IPTCEntryMeta.HEADLINE, headline);
    }

    /**
     * Liefert die IPTC-Felder 2:25 (Keywords).
     * 
     * @return IPTC-Felder 2:25 (Keywords) oder null, wenn nicht definiert
     * @see    Xmp#getDcSubjects()
     */
    public List<String> getKeywords() {
        return stringListOf(IPTCEntryMeta.KEYWORDS);
    }

    /**
     * Fügt ein IPTC-Feld 2:25 (Keywords) hinzu.
     * 
     * @param keyword IPTC-Feld 2:25 (Keyword)
     * @see           Xmp#addDcSubject(java.lang.String)
     */
    public void addKeyword(String keyword) {
        addToStringList(IPTCEntryMeta.KEYWORDS, keyword);
    }

    /**
     * Liefert das IPTC-Feld 2:05 (Object Name).
     * 
     * @return IPTC-Feld 2:05 (Object Name) oder null, wenn nicht definiert
     * @see    Xmp#getDcTitle()
     */
    public String getObjectName() {
        return stringValueOf(IPTCEntryMeta.OBJECT_NAME);
    }

    /**
     * Setzt das IPTC-Feld 2:05 (Object Name).
     * 
     * @param objectName IPTC-Feld 2:05 (Object Name)
     * @see              Xmp#setDcTitle(java.lang.String)
     */
    public void setObjectName(String objectName) {
        valueOfEntryMeta.put(IPTCEntryMeta.OBJECT_NAME, objectName);
    }

    /**
     * Liefert das IPTC-Feld 2:103 (Original Transmission Reference).
     * 
     * @return IPTC-Feld 2:103 (Original Transmission Reference) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopTransmissionReference()
     */
    public String getOriginalTransmissionReference() {
        return stringValueOf(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE);
    }

    /**
     * Setzt das IPTC-Feld 2:103 (Original Transmission Reference).
     * 
     * @param originalTransmissionReference IPTC-Feld 2:103 (Original Transmission Reference)
     * @see                                 Xmp#setPhotoshopTransmissionReference(java.lang.String)
     */
    public void setOriginalTransmissionReference(String originalTransmissionReference) {
        valueOfEntryMeta.put(IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE,
            originalTransmissionReference);
    }

    /**
     * Liefert das IPTC-Feld 2:95 (Province/State).
     * 
     * @return IPTC-Feld 2:95 (Province/State) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopState()
     */
    public String getProvinceState() {
        return stringValueOf(IPTCEntryMeta.PROVINCE_STATE);
    }

    /**
     * Setzt das IPTC-Feld 2:95 (Province/State).
     * 
     * @param provinceState IPTC-Feld 2:95 (Province/State)
     * @see                 Xmp#setPhotoshopState(java.lang.String)
     */
    public void setProvinceState(String provinceState) {
        valueOfEntryMeta.put(IPTCEntryMeta.PROVINCE_STATE, provinceState);
    }

    /**
     * Liefert das IPTC-Feld 2:115 (Source).
     * 
     * @return IPTC-Feld 2:115 (Source) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopSource()
     */
    public String getSource() {
        return stringValueOf(IPTCEntryMeta.SOURCE);
    }

    /**
     * Setzt das IPTC-Feld 2:115 (Source).
     * 
     * @param source IPTC-Feld 2:115 (Source)
     * @see          Xmp#setPhotoshopSource(java.lang.String)
     */
    public void setSource(String source) {
        valueOfEntryMeta.put(IPTCEntryMeta.SOURCE, source);
    }

    /**
     * Liefert das IPTC-Feld 2:40 (Special Instructions).
     * 
     * @return ITPTC-Feld 2:40 (Special Instructions) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopInstructions()
     */
    public String getSpecialInstructions() {
        return stringValueOf(IPTCEntryMeta.SPECIAL_INSTRUCTIONS);
    }

    /**
     * Setzt das IPTC-Feld 2:40 (Special Instructions).
     * 
     * @param specialInstructions IPTC-Feld 2:40 (Special Instructions)
     * @see                       Xmp#setPhotoshopInstructions(java.lang.String)
     */
    public void setSpecialInstructions(String specialInstructions) {
        valueOfEntryMeta.put(IPTCEntryMeta.SPECIAL_INSTRUCTIONS, specialInstructions);
    }

    /**
     * Liefert die IPTC-Felder 2:20 (Supplemental Category).
     * 
     * @return IPTC-Felder 2:20 (Supplemental Category) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopSupplementalCategories()
     */
    public List<String> getSupplementalCategories() {
        return stringListOf(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY);
    }

    /**
     * Fügt ein IPTC-Feld 2:20 (Supplemental Category) hinzu.
     * 
     * @param supplementalCategory IPTC-Feld 2:20 (Supplemental Category)
     * @see                        Xmp#addPhotoshopSupplementalCategory(java.lang.String)
     */
    public void addSupplementalCategory(String supplementalCategory) {
        addToStringList(IPTCEntryMeta.SUPPLEMENTAL_CATEGORY, supplementalCategory);
    }

    /**
     * Liefert die IPTC-Felder 2:122 (Writer/Editor).
     * 
     * @return IPTC-Felder 2:122 (Writer/Editor) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCaptionwriter()
     */
    public List<String> getWritersEditors() {
        return stringListOf(IPTCEntryMeta.WRITER_EDITOR);
    }

    /**
     * Fügt ein IPTC-Feld 2:122 (Writer/Editor) hinzu.
     * 
     * @param writerEditor IPTC-Feld 2:122 (Writer/Editor)
     * @see                Xmp#setPhotoshopCaptionwriter(java.lang.String)
     */
    public void addWriterEditor(String writerEditor) {
        addToStringList(IPTCEntryMeta.WRITER_EDITOR, writerEditor);
    }

    /**
     * Liefert den Wert eines IPTC-Metadatums.
     * 
     * @param  meta  IPTC-Metadatum
     * @return Wert: Ein String für sich nicht wiederholende Werte oder ein
     *         String-ArrayList für sich wiederholdende Werte oder null, wenn
     *         für dieses Metadatum kein Wert gesetzt ist
     */
    public Object getValue(IPTCEntryMeta meta) {
        return valueOfEntryMeta.get(meta);
    }

    /**
     * Setzt den Wert eines IPTC-Metadatums. Wiederholt sich der Wert, wird er
     * seinem Array hinzugefügt.
     * 
     * @param meta   IPTC-Metadatum
     * @param value  Wert
     */
    public void setValue(IPTCEntryMeta meta, String value) {
        if (IptcRepeatableValues.isRepeatable(meta)) {
            addToStringList(meta, value);
        } else {
            valueOfEntryMeta.put(meta, value);
        }
    }

    /**
     * Liefert, ob keine Daten enthalten sind.
     *
     * @return true, wenn keine Daten enthalten sind
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
            } else if (o != null) { // zuletzt, da leere Liste != null ist, aber trotzdem ein leeres Element
                return false;
            }
        }
        return true;
    }

    private String stringValueOf(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof String ? (String) o : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> stringListOf(IPTCEntryMeta meta) {
        Object o = valueOfEntryMeta.get(meta);
        return o instanceof List ? (List<String>) o : null;
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
