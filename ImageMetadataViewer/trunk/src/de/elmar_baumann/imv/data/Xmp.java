package de.elmar_baumann.imv.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpRepeatableValues;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.template.Pair;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * XMP-Metadaten einer Bilddatei. Unter den Operationen ist ein Link auf die
 * Operationen von IptcData, die dort das gleiche bedeuten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/22
 * @see     Iptc
 */
public final class Xmp {

    private final Map<Column, Object> valueOfColumn = new HashMap<Column, Object>();

    /**
     * Liefert das XMP-Felder dc:creator (Fotograf).
     * 
     * @return XMP-Feld dc:creator (Fotograf) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getByLines()
     */
    public String getDcCreator() {
        return stringValueOf(ColumnXmpDcCreator.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld dc:creator (Fotograf).
     * 
     * @param creator XMP-Feld dc:creator (Fotograf) ungleich null
     * @see           de.elmar_baumann.imv.data.Iptc#addByLine(java.lang.String)
     */
    public void setDcCreator(String creator) {
        valueOfColumn.put(ColumnXmpDcCreator.INSTANCE, creator);
    }

    /**
     * Liefert das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @return XMP-Feld dc:description (Bildbeschreibung) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCaptionAbstract()
     */
    public String getDcDescription() {
        return stringValueOf(ColumnXmpDcDescription.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @param dcDescription XMP-Feld dc:description (Bildbeschreibung)
     * @see                 de.elmar_baumann.imv.data.Iptc#setCaptionAbstract(java.lang.String)
     */
    public void setDcDescription(String dcDescription) {
        valueOfColumn.put(ColumnXmpDcDescription.INSTANCE, dcDescription);
    }

    /**
     * Liefert das XMP-Feld dc:rights (Copyright).
     * 
     * @return XMP-Feld dc:rights (Copyright) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCopyrightNotice()
     */
    public String getDcRights() {
        return stringValueOf(ColumnXmpDcRights.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld dc:rights (Copyright).
     * 
     * @param dcRights XMP-Feld dc:rights (Copyright)
     * @see            de.elmar_baumann.imv.data.Iptc#setCopyrightNotice(java.lang.String)
     */
    public void setDcRights(String dcRights) {
        valueOfColumn.put(ColumnXmpDcRights.INSTANCE, dcRights);
    }

    /**
     * Liefert die XMP-Felder dc:subject (Stichwörter).
     * 
     * @return XMP-Felder dc:subject (Stichwörter) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getKeywords()
     */
    public List<String> getDcSubjects() {
        return stringListOf(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    /**
     * Fügt ein XMP-Felder dc:subject (Stichwort) hinzu.
     * 
     * @param subject XMP-Felder dc:subject (Stichwort) ungleich null
     * @see           de.elmar_baumann.imv.data.Iptc#addKeyword(java.lang.String)
     */
    public void addDcSubject(String subject) {
        addToStringList(ColumnXmpDcSubjectsSubject.INSTANCE, subject);
    }

    /**
     * Liefert das XMP-Feld dc:title (Bezeichnung).
     * 
     * @return XMP-Feld dc:title (Bezeichnung) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getObjectName()
     */
    public String getDcTitle() {
        return stringValueOf(ColumnXmpDcTitle.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld dc:title (Bezeichnung).
     * 
     * @param dcTitle XMP-Feld dc:title (Bezeichnung)
     * @see           de.elmar_baumann.imv.data.Iptc#setObjectName(java.lang.String)
     */
    public void setDcTitle(String dcTitle) {
        valueOfColumn.put(ColumnXmpDcTitle.INSTANCE, dcTitle);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @return XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode) oder null wenn nicht definiert
     * @see             de.elmar_baumann.imv.data.Iptc#getContentLocationCodes()
     */
    public String getIptc4xmpcoreCountrycode() {
        return stringValueOf(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
    }

    /**
     * Setzt XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @param iptc4xmpcoreCountrycode XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode)
     */
    public void setIptc4xmpcoreCountrycode(String iptc4xmpcoreCountrycode) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, iptc4xmpcoreCountrycode);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @return XMP-Feld Iptc4xmpCore:Location (Ort) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getContentLocationNames()
     */
    public String getIptc4xmpcoreLocation() {
        return stringValueOf(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @param iptc4xmpcoreLocation XMP-Feld Iptc4xmpCore:Location (Ort)
     */
    public void setIptc4xmpcoreLocation(String iptc4xmpcoreLocation) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, iptc4xmpcoreLocation);
    }

    /**
     * Liefert XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @return XMP-Feld photoshop:AuthorsPosition (Position des Fotografen) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getByLinesTitles()
     */
    public String getPhotoshopAuthorsposition() {
        return stringValueOf(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @param photoshopAuthorsposition XMP-Feld photoshop:AuthorsPosition (Position des Fotografen)
     */
    public void setPhotoshopAuthorsposition(String photoshopAuthorsposition) {
        valueOfColumn.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, photoshopAuthorsposition);
    }

    /**
     * Liefert das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @return XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung) der Beschreibung oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getWritersEditors()
     */
    public String getPhotoshopCaptionwriter() {
        return stringValueOf(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @param photoshopCaptionwriter XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung)
     */
    public void setPhotoshopCaptionwriter(String photoshopCaptionwriter) {
        valueOfColumn.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, photoshopCaptionwriter);
    }

    /**
     * Liefert das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @return XMP-Feld photoshop:Category (Kategorie) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCategory()
     */
    public String getPhotoshopCategory() {
        return stringValueOf(ColumnXmpPhotoshopCategory.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @param photoshopCategory XMP-Feld photoshop:Category (Kategorie)
     * @see                     de.elmar_baumann.imv.data.Iptc#setCategory(java.lang.String)
     */
    public void setPhotoshopCategory(String photoshopCategory) {
        valueOfColumn.put(ColumnXmpPhotoshopCategory.INSTANCE, photoshopCategory);
    }

    /**
     * Liefert das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @return XMP-Feld photoshop:City (Stadt des Fotografen) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCity()
     */
    public String getPhotoshopCity() {
        return stringValueOf(ColumnXmpPhotoshopCity.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @param photoshopCity XMP-Feld photoshop:City (Stadt des Fotografen)
     * @see                 de.elmar_baumann.imv.data.Iptc#setCity(java.lang.String)
     */
    public void setPhotoshopCity(String photoshopCity) {
        valueOfColumn.put(ColumnXmpPhotoshopCity.INSTANCE, photoshopCity);
    }

    /**
     * Liefert das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @return XMP-Feld photoshop:Country (Land des Fotografen) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCountryPrimaryLocationName()
     */
    public String getPhotoshopCountry() {
        return stringValueOf(ColumnXmpPhotoshopCountry.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @param photoshopCountry XMP-Feld photoshop:Country (Land des Fotografen)
     * @see   de.elmar_baumann.imv.data.Iptc#setCountryPrimaryLocationName(java.lang.String)
     */
    public void setPhotoshopCountry(String photoshopCountry) {
        valueOfColumn.put(ColumnXmpPhotoshopCountry.INSTANCE, photoshopCountry);
    }

    /**
     * Liefert das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @return XMP-Feld photoshop:Credit (Anbieter) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getCredit()
     */
    public String getPhotoshopCredit() {
        return stringValueOf(ColumnXmpPhotoshopCredit.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @param photoshopCredit XMP-Feld photoshop:Credit (Anbieter)
     * @see   de.elmar_baumann.imv.data.Iptc#setCredit(java.lang.String)
     */
    public void setPhotoshopCredit(String photoshopCredit) {
        valueOfColumn.put(ColumnXmpPhotoshopCredit.INSTANCE, photoshopCredit);
    }

    /**
     * Liefert das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @return XMP-Feld photoshop:Headline (Bildtitel) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getHeadline()
     */
    public String getPhotoshopHeadline() {
        return stringValueOf(ColumnXmpPhotoshopHeadline.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @param photoshopHeadline XMP-Feld photoshop:Headline (Bildtitel)
     * @see                     de.elmar_baumann.imv.data.Iptc#setHeadline(java.lang.String)
     */
    public void setPhotoshopHeadline(String photoshopHeadline) {
        valueOfColumn.put(ColumnXmpPhotoshopHeadline.INSTANCE, photoshopHeadline);
    }

    /**
     * Liefert das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @return XMP-Feld photoshop:Instructions (Anweisungen) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getSpecialInstructions()
     */
    public String getPhotoshopInstructions() {
        return stringValueOf(ColumnXmpPhotoshopInstructions.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @param photoshopInstructions XMP-Feld photoshop:Instructions (Anweisungen)
     * @see                         de.elmar_baumann.imv.data.Iptc#setSpecialInstructions(java.lang.String)
     */
    public void setPhotoshopInstructions(String photoshopInstructions) {
        valueOfColumn.put(ColumnXmpPhotoshopInstructions.INSTANCE, photoshopInstructions);
    }

    /**
     * Liefert das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @return XMP-Feld photoshop:Source (Bildquelle) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getSource()
     */
    public String getPhotoshopSource() {
        return stringValueOf(ColumnXmpPhotoshopSource.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @param photoshopSource XMP-Feld photoshop:Source (Bildquelle)
     * @see                   de.elmar_baumann.imv.data.Iptc#setSource(java.lang.String)
     */
    public void setPhotoshopSource(String photoshopSource) {
        valueOfColumn.put(ColumnXmpPhotoshopSource.INSTANCE, photoshopSource);
    }

    /**
     * Liefert das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @return XMP-Feld photoshop:State (Bundesland des Fotografen) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getProvinceState()
     */
    public String getPhotoshopState() {
        return stringValueOf(ColumnXmpPhotoshopState.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @param photoshopState XMP-Feld photoshop:State (Bundesland des Fotografen)
     * @see                  de.elmar_baumann.imv.data.Iptc#setProvinceState(java.lang.String)
     */
    public void setPhotoshopState(String photoshopState) {
        valueOfColumn.put(ColumnXmpPhotoshopState.INSTANCE, photoshopState);
    }

    /**
     * Liefert die XMP-Felder photoshop:SupplementalCategories (weitere Kategorien).
     * 
     * @return XMP-Felder photoshop:SupplementalCategories (weitere Kategorien) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getSupplementalCategories()
     */
    public List<String> getPhotoshopSupplementalCategories() {
        return stringListOf(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
    }

    /**
     * Fügt ein XMP-Feld photoshop:SupplementalCategories (weitere Kategorien) hinzu.
     * 
     * @param category XMP-Feld photoshop:SupplementalCategories (weitere Kategorien)
     *                  ungleich null
     * @see            de.elmar_baumann.imv.data.Iptc#addSupplementalCategory(java.lang.String)
     */
    public void addPhotoshopSupplementalCategory(String category) {
        addToStringList(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, category);
    }

    /**
     * Liefert das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @return XMP-Feld photoshop:TransmissionReference (Auftragskennung) oder null wenn nicht definiert
     * @see    de.elmar_baumann.imv.data.Iptc#getOriginalTransmissionReference()
     */
    public String getPhotoshopTransmissionReference() {
        return stringValueOf(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
    }

    /**
     * Setzt das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @param photoshopTransmissionReference XMP-Feld photoshop:TransmissionReference (Auftragskennung)
     * @see                                  de.elmar_baumann.imv.data.Iptc#setOriginalTransmissionReference(java.lang.String)
     */
    public void setPhotoshopTransmissionReference(String photoshopTransmissionReference) {
        valueOfColumn.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, photoshopTransmissionReference);
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
                    boolean isSet = options.equals(SetIptc.REPLACE_EXISTING_VALUES) ||
                        getValue(xmpColumn) == null;
                    if (isSet) {
                        setValue(xmpColumn, iptcString);
                    }
                } else if (iptcValue instanceof List) {
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
                    AppLog.logWarning(Xmp.class, Bundle.getString("Xmp.ErrorMessage.SetIptc") + iptcValue + " (" + xmpColumn + ")"); // NOI18N
                }
            }
        }
    }

    /**
     * Liefert den Wert einer XMP-Spalte.
     * 
     * @param  xmpColumn  XMP-Spalte
     * @return Wert oder null, wenn nicht gesetzt. Aktuelle Werte:
     *         <ul>
     *         <li>String für alle der Spalte zugeordneten
     *             <code>set...()</code>-Methoden, die einen String setzen
     *         <li>String-Liste für alle der Spalte zugeordneten
     *             <code>add...()</code>-Methoden, die einen String
     *             hinzufügen (keywords, categories)
     *         <li>Long für alle der Spalte zugeordneten
     *             <code>set...()</code>-Methoden, die einen Long-Wert setzen
     *            (lastmodified)
     */
    public Object getValue(Column xmpColumn) {
        return valueOfColumn.get(xmpColumn);
    }

    /**
     * Setzt den Wert einer Spalte. Wiederholt sich der Wert, wird er seinem
     * Array hinzugefügt.
     * 
     * @param xmpColumn  XMP-Spalte
     * @param value      Wert
     */
    public void setValue(Column xmpColumn, String value) {
        if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
            addToStringList(xmpColumn, value);
        } else {
            valueOfColumn.put(xmpColumn, value);
        }
    }

    /**
     * Entfernt den Wert einer XMP-Spalte.
     * 
     * @param xmpColumn  XMP-Spalte
     * @param value      Wert ungleich null. Wird nur bei sich wiederholenden
     *                   Werten von Bedeutung: Dieser wird dann aus dem Werte-Array
     *                   entfernt
     */
    @SuppressWarnings("unchecked")
    public void removeValue(Column xmpColumn, String value) {
        Object o = valueOfColumn.get(xmpColumn);
        boolean remove = true;
        if (o instanceof List) {
            List list = (List) o;
            list.remove(value);
            remove = list.isEmpty();
        }
        if (remove) {
            valueOfColumn.remove(xmpColumn);
        }
    }

    /**
     * Entfernt alle Daten.
     */
    private void empty() {
        valueOfColumn.clear();
    }

    /**
     * Liefert, ob keine Daten enthalten sind.
     * 
     * @return true, wenn keine Daten enthalten sind
     */
    public boolean isEmpty() {
        for (Column column : valueOfColumn.keySet()) {
            Object o = valueOfColumn.get(column);
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

    private Long longValueOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof Long ? (Long) o : null;
    }

    private String stringValueOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof String ? (String) o : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> stringListOf(Column column) {
        Object o = valueOfColumn.get(column);
        return o instanceof List ? (List<String>) o : null;
    }

    private void addToStringList(Column column, String string) {
        if (string == null) return;
        List<String> list = stringListOf(column);
        if (list == null) {
            list = new ArrayList<String>();
            valueOfColumn.put(column, list);
        }
        if (!list.contains(string)) {
            list.add(string);
        }
    }
}
