package de.elmar_baumann.imv.data;

import com.imagero.reader.iptc.IPTCEntryMeta;
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
public class Xmp {

    private StringBuffer dcDescription = new StringBuffer();
    private StringBuffer dcRights = new StringBuffer();
    private StringBuffer dcTitle = new StringBuffer();
    private StringBuffer iptc4xmpcoreCountrycode = new StringBuffer();
    private StringBuffer iptc4xmpcoreLocation = new StringBuffer();
    private StringBuffer photoshopAuthorsposition = new StringBuffer();
    private StringBuffer photoshopCaptionwriter = new StringBuffer();
    private StringBuffer photoshopCategory = new StringBuffer();
    private StringBuffer photoshopCity = new StringBuffer();
    private StringBuffer photoshopCountry = new StringBuffer();
    private StringBuffer photoshopCredit = new StringBuffer();
    private StringBuffer photoshopHeadline = new StringBuffer();
    private StringBuffer photoshopInstructions = new StringBuffer();
    private StringBuffer photoshopSource = new StringBuffer();
    private StringBuffer photoshopState = new StringBuffer();
    private StringBuffer photoshopTransmissionReference = new StringBuffer();
    private StringBuffer dcCreator = new StringBuffer();
    private StringBuffer lastModified = new StringBuffer();
    private List<String> dcSubjects = new ArrayList<String>();
    private List<String> photoshopSupplementalCategories = new ArrayList<String>();
    private Map<Column, Object> valueOfColumn = new HashMap<Column, Object>();

    private void init() {
        valueOfColumn.put(ColumnXmpDcCreator.getInstance(),
            dcCreator);
        valueOfColumn.put(ColumnXmpDcDescription.getInstance(),
            dcDescription);
        valueOfColumn.put(ColumnXmpDcRights.getInstance(),
            dcRights);
        valueOfColumn.put(ColumnXmpDcSubjectsSubject.getInstance(),
            dcSubjects);
        valueOfColumn.put(ColumnXmpDcTitle.getInstance(),
            dcTitle);
        valueOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.getInstance(),
            iptc4xmpcoreCountrycode);
        valueOfColumn.put(ColumnXmpIptc4xmpcoreLocation.getInstance(),
            iptc4xmpcoreLocation);
        valueOfColumn.put(ColumnXmpPhotoshopAuthorsposition.getInstance(),
            photoshopAuthorsposition);
        valueOfColumn.put(ColumnXmpPhotoshopCaptionwriter.getInstance(),
            photoshopCaptionwriter);
        valueOfColumn.put(ColumnXmpPhotoshopCategory.getInstance(),
            photoshopCategory);
        valueOfColumn.put(ColumnXmpPhotoshopCity.getInstance(),
            photoshopCity);
        valueOfColumn.put(ColumnXmpPhotoshopCountry.getInstance(),
            photoshopCountry);
        valueOfColumn.put(ColumnXmpPhotoshopCredit.getInstance(),
            photoshopCredit);
        valueOfColumn.put(ColumnXmpPhotoshopHeadline.getInstance(),
            photoshopHeadline);
        valueOfColumn.put(ColumnXmpPhotoshopInstructions.getInstance(),
            photoshopInstructions);
        valueOfColumn.put(ColumnXmpPhotoshopSource.getInstance(),
            photoshopSource);
        valueOfColumn.put(ColumnXmpPhotoshopState.getInstance(),
            photoshopState);
        valueOfColumn.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(),
            photoshopSupplementalCategories);
        valueOfColumn.put(ColumnXmpPhotoshopTransmissionReference.getInstance(),
            photoshopTransmissionReference);
        valueOfColumn.put(ColumnXmpLastModified.getInstance(), lastModified);
    }

    public Xmp() {
        init();
    }

    /**
     * Liefert das XMP-Felder dc:creator (Fotograf).
     * 
     * @return XMP-Feld dc:creator (Fotograf) oder null wenn nicht definiert
     * @see    Iptc#getByLines()
     */
    public String getDcCreator() {
        return dcCreator.length() > 0
            ? dcCreator.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld dc:creator (Fotograf).
     * 
     * @param creator XMP-Feld dc:creator (Fotograf) ungleich null
     * @see           Iptc#addByLine(java.lang.String)
     */
    public void setDcCreator(String creator) {
        this.dcCreator.replace(0,
            this.dcCreator.length(), creator == null
            ? "" // NOI18N
            : creator);
    }

    /**
     * Liefert das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @return XMP-Feld dc:description (Bildbeschreibung) oder null wenn nicht definiert
     * @see    Iptc#getCaptionAbstract()
     */
    public String getDcDescription() {
        return dcDescription.length() > 0
            ? dcDescription.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld dc:description (Bildbeschreibung).
     * 
     * @param dcDescription XMP-Feld dc:description (Bildbeschreibung)
     * @see                 Iptc#setCaptionAbstract(java.lang.String)
     */
    public void setDcDescription(String dcDescription) {
        this.dcDescription.replace(0,
            this.dcDescription.length(), dcDescription == null
            ? "" // NOI18N
            : dcDescription);
    }

    /**
     * Liefert das XMP-Feld dc:rights (Copyright).
     * 
     * @return XMP-Feld dc:rights (Copyright) oder null wenn nicht definiert
     * @see    Iptc#getCopyrightNotice()
     */
    public String getDcRights() {
        return dcRights.length() > 0
            ? dcRights.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld dc:rights (Copyright).
     * 
     * @param dcRights XMP-Feld dc:rights (Copyright)
     * @see            Iptc#setCopyrightNotice(java.lang.String)
     */
    public void setDcRights(String dcRights) {
        this.dcRights.replace(0,
            this.dcRights.length(), dcRights == null
            ? "" // NOI18N
            : dcRights);
    }

    /**
     * Liefert die XMP-Felder dc:subject (Stichwörter).
     * 
     * @return XMP-Felder dc:subject (Stichwörter) oder null wenn nicht definiert
     * @see    Iptc#getKeywords()
     */
    public List<String> getDcSubjects() {
        return dcSubjects.isEmpty()
            ? null
            : dcSubjects;
    }

    /**
     * Fügt ein XMP-Felder dc:subject (Stichwort) hinzu.
     * 
     * @param subject XMP-Felder dc:subject (Stichwort) ungleich null
     * @see           Iptc#addKeyword(java.lang.String)
     */
    public void addDcSubject(String subject) {
        if (subject != null && !dcSubjects.contains(subject)) {
            dcSubjects.add(subject);
        }
    }

    /**
     * Entfernt ein Stichwort.
     * 
     * @param subject  Stichwort
     */
    public void removeDcSubject(String subject) {
        dcSubjects.remove(subject);
    }

    /**
     * Liefert das XMP-Feld dc:title (Bezeichnung).
     * 
     * @return XMP-Feld dc:title (Bezeichnung) oder null wenn nicht definiert
     * @see    Iptc#getObjectName()
     */
    public String getDcTitle() {
        return dcTitle.length() > 0
            ? dcTitle.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld dc:title (Bezeichnung).
     * 
     * @param dcTitle XMP-Feld dc:title (Bezeichnung)
     * @see           Iptc#setObjectName(java.lang.String)
     */
    public void setDcTitle(String dcTitle) {
        this.dcTitle.replace(0,
            this.dcTitle.length(), dcTitle == null
            ? "" // NOI18N
            : dcTitle);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @return XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode) oder null wenn nicht definiert
     * @see             Iptc#getContentLocationCodes()
     */
    public String getIptc4xmpcoreCountrycode() {
        return iptc4xmpcoreCountrycode.length() > 0
            ? iptc4xmpcoreCountrycode.toString()
            : null;
    }

    /**
     * Setzt XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode).
     * 
     * @param iptc4xmpcoreCountrycode XMP-Feld Iptc4xmpCore:CountryCode (ISO-Ländercode)
     */
    public void setIptc4xmpcoreCountrycode(String iptc4xmpcoreCountrycode) {
        this.iptc4xmpcoreCountrycode.replace(0,
            this.iptc4xmpcoreCountrycode.length(), iptc4xmpcoreCountrycode == null
            ? "" // NOI18N
            : iptc4xmpcoreCountrycode);
    }

    /**
     * Liefert das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @return XMP-Feld Iptc4xmpCore:Location (Ort) oder null wenn nicht definiert
     * @see    Iptc#getContentLocationNames()
     */
    public String getIptc4xmpcoreLocation() {
        return iptc4xmpcoreLocation.length() > 0
            ? iptc4xmpcoreLocation.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld Iptc4xmpCore:Location (Ort).
     * 
     * @param iptc4xmpcoreLocation XMP-Feld Iptc4xmpCore:Location (Ort)
     */
    public void setIptc4xmpcoreLocation(String iptc4xmpcoreLocation) {
        this.iptc4xmpcoreLocation.replace(0,
            this.iptc4xmpcoreLocation.length(), iptc4xmpcoreLocation == null
            ? "" // NOI18N
            : iptc4xmpcoreLocation);
    }

    /**
     * Liefert XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @return XMP-Feld photoshop:AuthorsPosition (Position des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getByLinesTitles()
     */
    public String getPhotoshopAuthorsposition() {
        return photoshopAuthorsposition.length() > 0
            ? photoshopAuthorsposition.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:AuthorsPosition (Position des Fotografen).
     * 
     * @param photoshopAuthorsposition XMP-Feld photoshop:AuthorsPosition (Position des Fotografen)
     */
    public void setPhotoshopAuthorsposition(String photoshopAuthorsposition) {
        this.photoshopAuthorsposition.replace(0,
            this.photoshopAuthorsposition.length(), photoshopAuthorsposition == null
            ? "" // NOI18N
            : photoshopAuthorsposition);
    }

    /**
     * Liefert das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @return XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung) der Beschreibung oder null wenn nicht definiert
     * @see    Iptc#getWritersEditors()
     */
    public String getPhotoshopCaptionwriter() {
        return photoshopCaptionwriter.length() > 0
            ? photoshopCaptionwriter.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung).
     * 
     * @param photoshopCaptionwriter XMP-Feld photoshop:CaptionWriter (Autor der Beschreibung)
     */
    public void setPhotoshopCaptionwriter(String photoshopCaptionwriter) {
        this.photoshopCaptionwriter.replace(0,
            this.photoshopCaptionwriter.length(), photoshopCaptionwriter == null
            ? "" // NOI18N
            : photoshopCaptionwriter);
    }

    /**
     * Liefert das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @return XMP-Feld photoshop:Category (Kategorie) oder null wenn nicht definiert
     * @see    Iptc#getCategory()
     */
    public String getPhotoshopCategory() {
        return photoshopCategory.length() > 0
            ? photoshopCategory.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Category (Kategorie).
     * 
     * @param photoshopCategory XMP-Feld photoshop:Category (Kategorie)
     * @see                     Iptc#setCategory(java.lang.String)
     */
    public void setPhotoshopCategory(String photoshopCategory) {
        this.photoshopCategory.replace(0,
            this.photoshopCategory.length(), photoshopCategory == null
            ? "" // NOI18N
            : photoshopCategory);
    }

    /**
     * Liefert das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @return XMP-Feld photoshop:City (Stadt des Fotografen) oder null wenn nicht definiert
     * @see Iptc#getCity()
     */
    public String getPhotoshopCity() {
        return photoshopCity.length() > 0
            ? photoshopCity.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:City (Stadt des Fotografen).
     * 
     * @param photoshopCity XMP-Feld photoshop:City (Stadt des Fotografen)
     * @see                 Iptc#setCity(java.lang.String)
     */
    public void setPhotoshopCity(String photoshopCity) {
        this.photoshopCity.replace(0,
            this.photoshopCity.length(), photoshopCity == null
            ? "" // NOI18N
            : photoshopCity);
    }

    /**
     * Liefert das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @return XMP-Feld photoshop:Country (Land des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getCountryPrimaryLocationName()
     */
    public String getPhotoshopCountry() {
        return photoshopCountry.length() > 0
            ? photoshopCountry.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Country (Land des Fotografen).
     * 
     * @param photoshopCountry XMP-Feld photoshop:Country (Land des Fotografen)
     * @see   Iptc#setCountryPrimaryLocationName(java.lang.String)
     */
    public void setPhotoshopCountry(String photoshopCountry) {
        this.photoshopCountry.replace(0,
            this.photoshopCountry.length(), photoshopCountry == null
            ? "" // NOI18N
            : photoshopCountry);
    }

    /**
     * Liefert das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @return XMP-Feld photoshop:Credit (Anbieter) oder null wenn nicht definiert
     * @see    Iptc#getCredit()
     */
    public String getPhotoshopCredit() {
        return photoshopCredit.length() > 0
            ? photoshopCredit.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Credit (Anbieter).
     * 
     * @param photoshopCredit XMP-Feld photoshop:Credit (Anbieter)
     * @see   Iptc#setCredit(java.lang.String)
     */
    public void setPhotoshopCredit(String photoshopCredit) {
        this.photoshopCredit.replace(0,
            this.photoshopCredit.length(), photoshopCredit == null
            ? "" // NOI18N
            : photoshopCredit);
    }

    /**
     * Liefert das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @return XMP-Feld photoshop:Headline (Bildtitel) oder null wenn nicht definiert
     * @see    Iptc#getHeadline()
     */
    public String getPhotoshopHeadline() {
        return photoshopHeadline.length() > 0
            ? photoshopHeadline.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Headline (Bildtitel).
     * 
     * @param photoshopHeadline XMP-Feld photoshop:Headline (Bildtitel)
     * @see                     Iptc#setHeadline(java.lang.String)
     */
    public void setPhotoshopHeadline(String photoshopHeadline) {
        this.photoshopHeadline.replace(0,
            this.photoshopHeadline.length(), photoshopHeadline == null
            ? "" // NOI18N
            : photoshopHeadline);
    }

    /**
     * Liefert das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @return XMP-Feld photoshop:Instructions (Anweisungen) oder null wenn nicht definiert
     * @see    Iptc#getSpecialInstructions()
     */
    public String getPhotoshopInstructions() {
        return photoshopInstructions.length() > 0
            ? photoshopInstructions.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Instructions (Anweisungen).
     * 
     * @param photoshopInstructions XMP-Feld photoshop:Instructions (Anweisungen)
     * @see                         Iptc#setSpecialInstructions(java.lang.String)
     */
    public void setPhotoshopInstructions(String photoshopInstructions) {
        this.photoshopInstructions.replace(0,
            this.photoshopInstructions.length(), photoshopInstructions == null
            ? "" // NOI18N
            : photoshopInstructions);
    }

    /**
     * Liefert das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @return XMP-Feld photoshop:Source (Bildquelle) oder null wenn nicht definiert
     * @see    Iptc#getSource()
     */
    public String getPhotoshopSource() {
        return photoshopSource.length() > 0
            ? photoshopSource.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:Source (Bildquelle).
     * 
     * @param photoshopSource XMP-Feld photoshop:Source (Bildquelle)
     * @see                   Iptc#setSource(java.lang.String)
     */
    public void setPhotoshopSource(String photoshopSource) {
        this.photoshopSource.replace(0,
            this.photoshopSource.length(), photoshopSource == null
            ? "" // NOI18N
            : photoshopSource);
    }

    /**
     * Liefert das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @return XMP-Feld photoshop:State (Bundesland des Fotografen) oder null wenn nicht definiert
     * @see    Iptc#getProvinceState()
     */
    public String getPhotoshopState() {
        return photoshopState.length() > 0
            ? photoshopState.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:State (Bundesland des Fotografen).
     * 
     * @param photoshopState XMP-Feld photoshop:State (Bundesland des Fotografen)
     * @see                  Iptc#setProvinceState(java.lang.String)
     */
    public void setPhotoshopState(String photoshopState) {
        this.photoshopState.replace(0,
            this.photoshopState.length(), photoshopState == null
            ? "" // NOI18N
            : photoshopState);
    }

    /**
     * Liefert die XMP-Felder photoshop:SupplementalCategories (weitere Kategorien).
     * 
     * @return XMP-Felder photoshop:SupplementalCategories (weitere Kategorien) oder null wenn nicht definiert
     * @see    Iptc#getSupplementalCategories()
     */
    public List<String> getPhotoshopSupplementalCategories() {
        return photoshopSupplementalCategories.isEmpty()
            ? null
            : photoshopSupplementalCategories;
    }

    /**
     * Fügt ein XMP-Feld photoshop:SupplementalCategories (weitere Kategorien) hinzu.
     * 
     * @param category XMP-Feld photoshop:SupplementalCategories (weitere Kategorien)
     *                  ungleich null
     * @see            Iptc#addSupplementalCategory(java.lang.String)
     */
    public void addPhotoshopSupplementalCategory(String category) {
        if (category != null && !photoshopSupplementalCategories.contains(category)) {
            photoshopSupplementalCategories.add(category);
        }
    }

    /**
     * Entfernt eine weitere Kategorie.
     * 
     * @param category  Kategorie
     */
    public void removePhotoshopSupplementalCategory(String category) {
        photoshopSupplementalCategories.remove(category);
    }

    /**
     * Liefert das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @return XMP-Feld photoshop:TransmissionReference (Auftragskennung) oder null wenn nicht definiert
     * @see    Iptc#getOriginalTransmissionReference()
     */
    public String getPhotoshopTransmissionReference() {
        return photoshopTransmissionReference.length() > 0
            ? photoshopTransmissionReference.toString()
            : null;
    }

    /**
     * Setzt das XMP-Feld photoshop:TransmissionReference (Auftragskennung).
     * 
     * @param photoshopTransmissionReference XMP-Feld photoshop:TransmissionReference (Auftragskennung)
     * @see                                  Iptc#setOriginalTransmissionReference(java.lang.String)
     */
    public void setPhotoshopTransmissionReference(String photoshopTransmissionReference) {
        this.photoshopTransmissionReference.replace(0,
            this.photoshopTransmissionReference.length(),
            photoshopTransmissionReference == null
            ? "" // NOI18N
            : photoshopTransmissionReference);
    }

    /**
     * Sets the last modification time of the XMP data.
     * 
     * @param lastModified  milliseconds since 1970 of the modification time
     */
    public void setLastModified(long lastModified) {
        this.lastModified.replace(0, this.lastModified.length(), lastModified >= 0
            ? Long.toString(lastModified)
            : ""); // NOI18N
    }

    /**
     * Returns the last modification time of the XMP data.
     * 
     * @return milliseconds since 1970 of the modification time or null
     *         if not defined
     */
    public Long getLastModified() {
        return lastModified.length() > 0
            ? Long.parseLong(lastModified.toString())
            : null;
    }

    public void setIptc(Iptc iptc, boolean replaceExistingValues) {
        if (replaceExistingValues) {
            empty();
        }
        Set<Column> xmpColumns = valueOfColumn.keySet();
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        for (Column xmpColumn : xmpColumns) {
            IPTCEntryMeta iptcEntryMeta = mapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            Object iptcValue = iptc.getValue(iptcEntryMeta);
            if (iptcValue != null) {
                if (iptcValue instanceof String) {
                    String string = (String) iptcValue;
                    boolean replace = replaceExistingValues || getValue(xmpColumn) == null;
                    if (replace) {
                        setValue(xmpColumn, string);
                    }
                } else if (iptcValue instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> array = (List<String>) iptcValue;
                    for (String string : array) {
                        setValue(xmpColumn, string);
                    }
                }
            }
        }
    }

    /**
     * Liefert den Wert einer XMP-Spalte.
     * 
     * @param  xmpColumn  XMP-Spalte
     * @return Wert: Ein String für sich nicht wiederholende Werte oder eine
     *         String-ArrayList für sich wiederholdende Werte oder null, wenn
     *         für diese Spalte kein Wert gesetzt ist
     */
    public Object getValue(Column xmpColumn) {
        Object value = valueOfColumn.get(xmpColumn);
        if (value instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer) value;
            if (stringBuffer.length() <= 0) {
                return null;
            } else {
                return stringBuffer.toString();
            }
        } else if (value instanceof List) {
            List array = (List) value;
            if (array.isEmpty()) {
                return null;
            } else {
                return array;
            }
        }
        return null;
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
        if (o instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer) o;
            stringBuffer.replace(0, stringBuffer.length(), value == null
                ? "" // NOI18N
                : value);
        } else if (o instanceof List && value != null) {
            List array = (List) o;
            if (!array.contains(value)) {
                array.add(value);
            }
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
        if (o instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer) o;
            stringBuffer.replace(0, stringBuffer.length(), ""); // NOI18N
        } else if (o instanceof List) {
            List array = (List) o;
            array.remove(value);
        }
    }

    /**
     * Entfernt alle Daten.
     */
    void empty() {
        Set<Column> columns = valueOfColumn.keySet();
        for (Column column : columns) {
            Object value = valueOfColumn.get(column);
            if (value instanceof StringBuffer) {
                StringBuffer stringBuffer = (StringBuffer) value;
                stringBuffer.replace(0, stringBuffer.length(), ""); // NOI18N
            } else if (value instanceof List) {
                List array = (List) value;
                array.clear();
            }
        }
    }

    /**
     * Liefert, ob keine Daten enthalten sind.
     * 
     * @return true, wenn keine Daten enthalten sind
     */
    public boolean isEmpty() {
        return dcDescription.length() <= 0 &&
            dcCreator.length() <= 0 &&
            dcRights.length() <= 0 &&
            dcTitle.length() <= 0 &&
            iptc4xmpcoreCountrycode.length() <= 0 &&
            iptc4xmpcoreLocation.length() <= 0 &&
            photoshopAuthorsposition.length() <= 0 &&
            photoshopCaptionwriter.length() <= 0 &&
            photoshopCategory.length() <= 0 &&
            photoshopCity.length() <= 0 &&
            photoshopCountry.length() <= 0 &&
            photoshopCredit.length() <= 0 &&
            photoshopHeadline.length() <= 0 &&
            photoshopInstructions.length() <= 0 &&
            photoshopSource.length() <= 0 &&
            photoshopState.length() <= 0 &&
            photoshopTransmissionReference.length() <= 0 &&
            dcSubjects.isEmpty() &&
            photoshopSupplementalCategories.isEmpty();
    }
}
