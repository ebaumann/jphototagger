package de.elmar_baumann.imv.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.mapping.IptcXmpMapping;
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
import java.util.List;
import java.util.HashMap;
import java.util.Set;
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

    public enum SetIptc {

        REPLACE_EXISTING_VALUES, DONT_CHANGE_EXISTING_VALUES
    };

    /**
     * Liefert das XMP-Felder dc:creator (Fotograf).
     * 
     * @return XMP-Feld dc:creator (Fotograf) oder null wenn nicht definiert
     * @see    Iptc#getByLines()
     */
    public String getDcCreator() {
        return stringValueOf(ColumnXmpDcCreator.getInstance());
    }

    /**
     * Setzt das XMP-Feld dc:creator (Fotograf).
     * 
     * @param creator XMP-Feld dc:creator (Fotograf) ungleich null
     * @see           Iptc#addByLine(java.lang.String)
     */
    public void setDcCreator(String creator) {
        valueOfColumn.put(ColumnXmpDcCreator.getInstance(), creator);
    }

    /**
     * Liefert das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @return XMP-Feld dc:description (Bildbeschreibung) oder null wenn nicht definiert
     * @see    Iptc#getCaptionAbstract()
     */
    public String getDcDescription() {
        return stringValueOf(ColumnXmpDcDescription.getInstance());
    }

    /**
     * Setzt das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @param dcDescription XMP-Feld dc:description (Bildbeschreibung)
     * @see                 Iptc#setCaptionAbstract(java.lang.String)
     */
    public void setDcDescription(String dcDescription) {
        valueOfColumn.put(ColumnXmpDcDescription.getInstance(), dcDescription);
    }

    /**
     * Liefert das XMP-Feld dc:rights (Copyright).
     * 
     * @return XMP-Feld dc:rights (Copyright) oder null wenn nicht definiert
     * @see    Iptc#getCopyrightNotice()
     */
    public String getDcRights() {
        return stringValueOf(ColumnXmpDcRights.getInstance());
    }

    /**
     * Setzt das XMP-Feld dc:rights (Copyright).
     * 
     * @param dcRights XMP-Feld dc:rights (Copyright)
     * @see            Iptc#setCopyrightNotice(java.lang.String)
     */
    public void setDcRights(String dcRights) {
        valueOfColumn.put(ColumnXmpDcRights.getInstance(), dcRights);
    }

    /**
     * Liefert die XMP-Felder dc:subject (Stichwörter).
     * 
     * @return XMP-Felder dc:subject (Stichwörter) oder null wenn nicht definiert
     * @see    Iptc#getKeywords()
     */
    public List<String> getDcSubjects() {
        return stringListOf(ColumnXmpDcSubjectsSubject.getInstance());
    }

    /**
     * Fügt ein XMP-Felder dc:subject (Stichwort) hinzu.
     * 
     * @param subject XMP-Felder dc:subject (Stichwort) ungleich null
     * @see           Iptc#addKeyword(java.lang.String)
     */
    public void addDcSubject(String subject) {
        addToStringList(ColumnXmpDcSubjectsSubject.getInstance(), subject);
    }

    /**
     * Liefert das XMP-Feld dc:title (Bezeichnung).
     * 
     * @return XMP-Feld dc:title (Bezeichnung) oder null wenn nicht definiert
     * @see    Iptc#getObjectName()
     */
    public String getDcTitle() {
        return stringValueOf(ColumnXmpDcTitle.getInstance());
    }

    /**
     * Setzt das XMP-Feld dc:title (Bezeichnung).
     * 
     * @param dcTitle XMP-Feld dc:title (Bezeichnung)
     * @see           Iptc#setObjectName(java.lang.String)
     */
    public void setDcTitle(String dcTitle) {
        valueOfColumn.put(ColumnXmpDcTitle.getInstance(), dcTitle);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @return XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode) oder null wenn nicht definiert
     * @see             Iptc#getContentLocationCodes()
     */
    public String getIptc4xmpcoreCountrycode() {
        return stringValueOf(ColumnXmpIptc4xmpcoreCountrycode.getInstance());
    }

    /**
     * Setzt XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @param iptc4xmpcoreCountrycode XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode)
     */
    public void setIptc4xmpcoreCountrycode(String iptc4xmpcoreCountrycode) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.getInstance(), iptc4xmpcoreCountrycode);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @return XMP-Feld Iptc4xmpCore:Location (Ort) oder null wenn nicht definiert
     * @see    Iptc#getContentLocationNames()
     */
    public String getIptc4xmpcoreLocation() {
        return stringValueOf(ColumnXmpIptc4xmpcoreLocation.getInstance());
    }

    /**
     * Setzt das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @param iptc4xmpcoreLocation XMP-Feld Iptc4xmpCore:Location (Ort)
     */
    public void setIptc4xmpcoreLocation(String iptc4xmpcoreLocation) {
        valueOfColumn.put(ColumnXmpIptc4xmpcoreLocation.getInstance(), iptc4xmpcoreLocation);
    }

    /**
     * Liefert XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @return XMP-Feld photoshop:AuthorsPosition (Position des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getByLinesTitles()
     */
    public String getPhotoshopAuthorsposition() {
        return stringValueOf(ColumnXmpPhotoshopAuthorsposition.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @param photoshopAuthorsposition XMP-Feld photoshop:AuthorsPosition (Position des Fotografen)
     */
    public void setPhotoshopAuthorsposition(String photoshopAuthorsposition) {
        valueOfColumn.put(ColumnXmpPhotoshopAuthorsposition.getInstance(), photoshopAuthorsposition);
    }

    /**
     * Liefert das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @return XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung) der Beschreibung oder null wenn nicht definiert
     * @see    Iptc#getWritersEditors()
     */
    public String getPhotoshopCaptionwriter() {
        return stringValueOf(ColumnXmpPhotoshopCaptionwriter.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @param photoshopCaptionwriter XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung)
     */
    public void setPhotoshopCaptionwriter(String photoshopCaptionwriter) {
        valueOfColumn.put(ColumnXmpPhotoshopCaptionwriter.getInstance(), photoshopCaptionwriter);
    }

    /**
     * Liefert das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @return XMP-Feld photoshop:Category (Kategorie) oder null wenn nicht definiert
     * @see    Iptc#getCategory()
     */
    public String getPhotoshopCategory() {
        return stringValueOf(ColumnXmpPhotoshopCategory.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @param photoshopCategory XMP-Feld photoshop:Category (Kategorie)
     * @see                     Iptc#setCategory(java.lang.String)
     */
    public void setPhotoshopCategory(String photoshopCategory) {
        valueOfColumn.put(ColumnXmpPhotoshopCategory.getInstance(), photoshopCategory);
    }

    /**
     * Liefert das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @return XMP-Feld photoshop:City (Stadt des Fotografen) oder null wenn nicht definiert
     * @see Iptc#getCity()
     */
    public String getPhotoshopCity() {
        return stringValueOf(ColumnXmpPhotoshopCity.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @param photoshopCity XMP-Feld photoshop:City (Stadt des Fotografen)
     * @see                 Iptc#setCity(java.lang.String)
     */
    public void setPhotoshopCity(String photoshopCity) {
        valueOfColumn.put(ColumnXmpPhotoshopCity.getInstance(), photoshopCity);
    }

    /**
     * Liefert das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @return XMP-Feld photoshop:Country (Land des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getCountryPrimaryLocationName()
     */
    public String getPhotoshopCountry() {
        return stringValueOf(ColumnXmpPhotoshopCountry.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @param photoshopCountry XMP-Feld photoshop:Country (Land des Fotografen)
     * @see   Iptc#setCountryPrimaryLocationName(java.lang.String)
     */
    public void setPhotoshopCountry(String photoshopCountry) {
        valueOfColumn.put(ColumnXmpPhotoshopCountry.getInstance(), photoshopCountry);
    }

    /**
     * Liefert das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @return XMP-Feld photoshop:Credit (Anbieter) oder null wenn nicht definiert
     * @see    Iptc#getCredit()
     */
    public String getPhotoshopCredit() {
        return stringValueOf(ColumnXmpPhotoshopCredit.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @param photoshopCredit XMP-Feld photoshop:Credit (Anbieter)
     * @see   Iptc#setCredit(java.lang.String)
     */
    public void setPhotoshopCredit(String photoshopCredit) {
        valueOfColumn.put(ColumnXmpPhotoshopCredit.getInstance(), photoshopCredit);
    }

    /**
     * Liefert das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @return XMP-Feld photoshop:Headline (Bildtitel) oder null wenn nicht definiert
     * @see    Iptc#getHeadline()
     */
    public String getPhotoshopHeadline() {
        return stringValueOf(ColumnXmpPhotoshopHeadline.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @param photoshopHeadline XMP-Feld photoshop:Headline (Bildtitel)
     * @see                     Iptc#setHeadline(java.lang.String)
     */
    public void setPhotoshopHeadline(String photoshopHeadline) {
        valueOfColumn.put(ColumnXmpPhotoshopHeadline.getInstance(), photoshopHeadline);
    }

    /**
     * Liefert das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @return XMP-Feld photoshop:Instructions (Anweisungen) oder null wenn nicht definiert
     * @see    Iptc#getSpecialInstructions()
     */
    public String getPhotoshopInstructions() {
        return stringValueOf(ColumnXmpPhotoshopInstructions.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @param photoshopInstructions XMP-Feld photoshop:Instructions (Anweisungen)
     * @see                         Iptc#setSpecialInstructions(java.lang.String)
     */
    public void setPhotoshopInstructions(String photoshopInstructions) {
        valueOfColumn.put(ColumnXmpPhotoshopInstructions.getInstance(), photoshopInstructions);
    }

    /**
     * Liefert das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @return XMP-Feld photoshop:Source (Bildquelle) oder null wenn nicht definiert
     * @see    Iptc#getSource()
     */
    public String getPhotoshopSource() {
        return stringValueOf(ColumnXmpPhotoshopSource.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @param photoshopSource XMP-Feld photoshop:Source (Bildquelle)
     * @see                   Iptc#setSource(java.lang.String)
     */
    public void setPhotoshopSource(String photoshopSource) {
        valueOfColumn.put(ColumnXmpPhotoshopSource.getInstance(), photoshopSource);
    }

    /**
     * Liefert das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @return XMP-Feld photoshop:State (Bundesland des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getProvinceState()
     */
    public String getPhotoshopState() {
        return stringValueOf(ColumnXmpPhotoshopState.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @param photoshopState XMP-Feld photoshop:State (Bundesland des Fotografen)
     * @see                  Iptc#setProvinceState(java.lang.String)
     */
    public void setPhotoshopState(String photoshopState) {
        valueOfColumn.put(ColumnXmpPhotoshopState.getInstance(), photoshopState);
    }

    /**
     * Liefert die XMP-Felder photoshop:SupplementalCategories (weitere Kategorien).
     * 
     * @return XMP-Felder photoshop:SupplementalCategories (weitere Kategorien) oder null wenn nicht definiert
     * @see    Iptc#getSupplementalCategories()
     */
    public List<String> getPhotoshopSupplementalCategories() {
        return stringListOf(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
    }

    /**
     * Fügt ein XMP-Feld photoshop:SupplementalCategories (weitere Kategorien) hinzu.
     * 
     * @param category XMP-Feld photoshop:SupplementalCategories (weitere Kategorien)
     *                  ungleich null
     * @see            Iptc#addSupplementalCategory(java.lang.String)
     */
    public void addPhotoshopSupplementalCategory(String category) {
        addToStringList(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), category);
    }

    /**
     * Liefert das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @return XMP-Feld photoshop:TransmissionReference (Auftragskennung) oder null wenn nicht definiert
     * @see    Iptc#getOriginalTransmissionReference()
     */
    public String getPhotoshopTransmissionReference() {
        return stringValueOf(ColumnXmpPhotoshopTransmissionReference.getInstance());
    }

    /**
     * Setzt das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @param photoshopTransmissionReference XMP-Feld photoshop:TransmissionReference (Auftragskennung)
     * @see                                  Iptc#setOriginalTransmissionReference(java.lang.String)
     */
    public void setPhotoshopTransmissionReference(String photoshopTransmissionReference) {
        valueOfColumn.put(ColumnXmpPhotoshopTransmissionReference.getInstance(), photoshopTransmissionReference);
    }

    /**
     * Sets the last modification time of the XMP data.
     * 
     * @param lastModified  milliseconds since 1970 of the modification time
     */
    public void setLastModified(long lastModified) {
        valueOfColumn.put(ColumnXmpLastModified.getInstance(), lastModified);
    }

    /**
     * Returns the last modification time of the XMP data.
     * 
     * @return milliseconds since 1970 of the modification time or null
     *         if not defined
     */
    public Long getLastModified() {
        return longValueOf(ColumnXmpLastModified.getInstance());
    }

    public void setIptc(Iptc iptc, SetIptc options) {
        if (options.equals(SetIptc.REPLACE_EXISTING_VALUES)) {
            empty();
        }
        Set<Column> xmpColumns = valueOfColumn.keySet();
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        for (Column xmpColumn : xmpColumns) {
            IPTCEntryMeta iptcEntryMeta = mapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            Object iptcValue = iptc.getValue(iptcEntryMeta);
            if (iptcValue != null) {
                if (iptcValue instanceof String) {
                    String iptcString = (String) iptcValue;
                    boolean replace = options.equals(
                            SetIptc.REPLACE_EXISTING_VALUES) ||
                            getValue(xmpColumn) == null;
                    if (replace) {
                        setValue(xmpColumn, iptcString);
                    }
                } else if (iptcValue instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> array = (List<String>) iptcValue;
                    for (String string : array) {
                        setValue(xmpColumn, string);
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
    @SuppressWarnings("unchecked")
    public void setValue(Column xmpColumn, String value) {
        Object o = valueOfColumn.get(xmpColumn);
        if (o instanceof String) {
            valueOfColumn.put(xmpColumn, value);
        } else if (o instanceof List && value != null) {
            List list = (List) o;
            if (!list.contains(value)) {
                list.add(value);
            }
        } else {
            AppLog.logWarning(Xmp.class, Bundle.getString("Xmp.ErrorMessage.SetValue") + value + " (" + xmpColumn + ")"); // NOI18N
        }
    }

    /**
     * Entfernt den Wert einer XMP-Spalte.
     * 
     * @param xmpColumn  XMP-Spalte
     * @param value      Wert ungleich null
     */
    @SuppressWarnings("unchecked")
    public void removeValue(Column xmpColumn, String value) {
        Object o = valueOfColumn.get(xmpColumn);
        boolean remove = o != null;
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
    void empty() {
        valueOfColumn.clear();
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

    private void addToStringList(Column column, String s) {
        if (s == null)
            return;
        List<String> list = stringListOf(column);
        if (list == null) {
            list = new ArrayList<String>();
            valueOfColumn.put(column, list);
        }
        if (!list.contains(s)) {
            list.add(s);
        }
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
                if (!string.trim().isEmpty())
                    return false;
            } else if (o instanceof List) {
                List list = (List) o;
                if (!list.isEmpty())
                    return false;
            } else if (o != null) { // zuletzt, da leere Liste != null ist, aber trotzdem ein leeres Element
                return false;
            }
        }
        return true;
    }
}
