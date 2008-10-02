package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesByLine;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesTitlesByLineTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCaptionAbstract;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationCodesContentLocationCode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationNamesContentLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCopyrightNotice;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCountryPrimaryLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCreationDate;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCredit;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcKeywordsKeyword;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcObjectName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcOriginalTransmissionReference;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcProvinceState;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSource;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSpecialInstructions;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSupplementalCategoriesSupplementalCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcWritersEditorsWriterEditor;
import java.sql.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * IPTC-Metadaten einer Bilddatei. Unter den Operationen ist ein Link auf die
 * Operationen von XmpData, die dort das gleiche bedeuten.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/28
 * @see     Xmp
 */
public class Iptc {

    private Vector<String> byLines = new Vector<String>();
    private Vector<String> byLinesTitles = new Vector<String>();
    private StringBuffer captionAbstract = new StringBuffer();
    private StringBuffer category = new StringBuffer();
    private StringBuffer city = new StringBuffer();
    private Vector<String> contentLocationCodes = new Vector<String>();
    private Vector<String> contentLocationNames = new Vector<String>();
    private StringBuffer copyrightNotice = new StringBuffer();
    private StringBuffer countryPrimaryLocationName = new StringBuffer();
    private Date creationDate;
    private StringBuffer credit = new StringBuffer();
    private StringBuffer headline = new StringBuffer();
    private Vector<String> keywords = new Vector<String>();
    private StringBuffer objectName = new StringBuffer();
    private StringBuffer originalTransmissionReference = new StringBuffer();
    private StringBuffer provinceState = new StringBuffer();
    private StringBuffer source = new StringBuffer();
    private StringBuffer specialInstructions = new StringBuffer();
    private Vector<String> supplementalCategories = new Vector<String>();
    private Vector<String> writersEditors = new Vector<String>();
    private HashMap<Column, Object> valueOfColumn = new HashMap<Column, Object>();

    private void init() {
        valueOfColumn.put(ColumnIptcCopyrightNotice.getInstance(), copyrightNotice);
        valueOfColumn.put(ColumnIptcCaptionAbstract.getInstance(), captionAbstract);
        valueOfColumn.put(ColumnIptcObjectName.getInstance(), objectName);
        valueOfColumn.put(ColumnIptcHeadline.getInstance(), headline);
        valueOfColumn.put(ColumnIptcCategory.getInstance(), category);
        valueOfColumn.put(ColumnIptcCity.getInstance(), city);
        valueOfColumn.put(ColumnIptcProvinceState.getInstance(), provinceState);
        valueOfColumn.put(ColumnIptcCountryPrimaryLocationName.getInstance(), countryPrimaryLocationName);
        valueOfColumn.put(ColumnIptcOriginalTransmissionReference.getInstance(), originalTransmissionReference);
        valueOfColumn.put(ColumnIptcSpecialInstructions.getInstance(), specialInstructions);
        valueOfColumn.put(ColumnIptcCredit.getInstance(), credit);
        valueOfColumn.put(ColumnIptcSource.getInstance(), source);
        valueOfColumn.put(ColumnIptcKeywordsKeyword.getInstance(), keywords);
        valueOfColumn.put(ColumnIptcByLinesByLine.getInstance(), byLines);
        valueOfColumn.put(ColumnIptcContentLocationNamesContentLocationName.getInstance(), contentLocationNames);
        valueOfColumn.put(ColumnIptcContentLocationCodesContentLocationCode.getInstance(), contentLocationCodes);
        valueOfColumn.put(ColumnIptcWritersEditorsWriterEditor.getInstance(), writersEditors);
        valueOfColumn.put(ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance(), supplementalCategories);
        valueOfColumn.put(ColumnIptcByLinesTitlesByLineTitle.getInstance(), byLinesTitles);
        valueOfColumn.put(ColumnIptcCreationDate.getInstance(), creationDate);
    }

    public Iptc() {
        init();
    }

    /**
     * Liefert die IPTC-Felder 2:85 (By-line Title).
     * 
     * @return IPTC-Felder 2:85 oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopAuthorsposition()
     */
    public Vector<String> getByLinesTitles() {
        return byLinesTitles.isEmpty() ? null : byLinesTitles;
    }

    /**
     * Fügt ein IPTC-Feld 2:85 (By-line Title) hinzu.
     * 
     * @param byLineTitle IPTC-Feld 2:85 (By-line Title)
     * @see               Xmp#setPhotoshopAuthorsposition(java.lang.String)
     */
    public void addByLineTitle(String byLineTitle) {
        if (byLineTitle != null && !byLinesTitles.contains(byLineTitle)) {
            byLinesTitles.add(byLineTitle);
        }
    }

    /**
     * Liefert die IPTC-Felder 2:80 (Byline).
     * 
     * @return IPTC-Felder 2:80 (Byline) oder null, wenn nicht definiert
     * @see    Xmp#getDcCreators()
     */
    public Vector<String> getByLines() {
        return byLines.isEmpty() ? null : byLines;
    }

    /**
     * Fügt ein IPTC-Feld 2:80 (Byline) hinzu.
     * 
     * @param byLine IPTC-Feld 2:80 (Byline)
     * @see          Xmp#addDcCreator(java.lang.String)
     */
    public void addByLine(String byLine) {
        if (byLine != null && !byLines.contains(byLine)) {
            byLines.add(byLine);
        }
    }

    /**
     * Liefert das IPTC-Feld 2:120 (Caption/Abstract).
     * 
     * @return IPTC-Feld 2:120 (Caption/Abstract) oder null, wenn nicht definiert
     * @see    Xmp#getDcDescription()
     */
    public String getCaptionAbstract() {
        return captionAbstract.length() <= 0 ? null : captionAbstract.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:120 (Caption/Abstract).
     * 
     * @param captionAbstract IPTC-Feld 2:120 (Caption/Abstract)
     * @see                   Xmp#setDcDescription(java.lang.String)
     */
    public void setCaptionAbstract(String captionAbstract) {
        this.captionAbstract.replace(0,
            this.captionAbstract.length(),
            captionAbstract == null
            ? "" // NOI18N
            : captionAbstract);
    }

    /**
     * Liefert das IPTC-Feld 2:15 (Category).
     * 
     * @return IPTC-Feld 2:15 (Category) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCategory()
     */
    public String getCategory() {
        return category.length() <= 0 ? null : category.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:15 (Category).
     * 
     * @param category IPTC-Feld 2:15 (Category)
     * @see            Xmp#setPhotoshopCategory(java.lang.String)
     */
    public void setCategory(String category) {
        this.category.replace(0,
            this.category.length(),
            category == null
            ? "" // NOI18N
            : category);
    }

    /**
     * Liefert das IPTC-Feld 2:90 (City).
     * 
     * @return IPTC-Feld 2:90 (City) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCity()
     */
    public String getCity() {
        return city.length() <= 0 ? null : city.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:90 (City).
     * 
     * @param city IPTC-Feld 2:90 (City)
     * @see        Xmp#setPhotoshopCity(java.lang.String)
     */
    public void setCity(String city) {
        this.city.replace(0, this.city.length(),
            city == null
            ? "" // NOI18N
            : city);
    }

    /**
     * Liefert die IPTC-Felder 2:26 (Content Location Code).
     * 
     * @return IPTC-Felder 2:26 (Content Location Code) oder null, wenn nicht definiert
     * @see    Xmp#getIptc4xmpcoreCountrycode()
     */
    public Vector<String> getContentLocationCodes() {
        return contentLocationCodes.isEmpty() ? null : contentLocationCodes;
    }

    /**
     * Fügt ein IPTC-Feld 2:26 (Content Location Code) hinzu.
     * 
     * @param contentLocationCode IPTC-Feld 2:26 (Content Location Code)
     * @see                       Xmp#setIptc4xmpcoreCountrycode(java.lang.String)
     */
    public void addContentLocationCode(String contentLocationCode) {
        if (contentLocationCode != null &&
            !contentLocationCodes.contains(contentLocationCode)) {
            contentLocationCodes.add(contentLocationCode);
        }
    }

    /**
     * Liefert die IPTC-Felder 2:27 (Content Location Name).
     * 
     * @return IPTC-Felder 2:27 (Content Location Name) oder null, wenn nicht definiert
     * @see    Xmp#getIptc4xmpcoreLocation()
     */
    public Vector<String> getContentLocationNames() {
        return contentLocationNames.isEmpty() ? null : contentLocationNames;
    }

    /**
     * Fügt ein IPTC-Feld 2:27 (Content Location Name) hinzu.
     * 
     * @param contentLocationName IPTC-Feld 2:27 (Content Location Name)
     * @see                       Xmp#setIptc4xmpcoreLocation(java.lang.String)
     */
    public void addContentLocationName(String contentLocationName) {
        if (contentLocationName != null &&
            !contentLocationNames.contains(contentLocationName)) {
            contentLocationNames.add(contentLocationName);
        }
    }

    /**
     * Liefert das IPTC-Feld 2:116 (Copyright Notice).
     * 
     * @return IPTC-Feld 2:116 (Copyright Notice) oder null, wenn nicht definiert
     * @see    Xmp#getDcRights()
     */
    public String getCopyrightNotice() {
        return copyrightNotice.length() <= 0 ? null : copyrightNotice.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:116 (Copyright Notice).
     * 
     * @param copyrightNotice IPTC-Feld 2:116 (Copyright Notice)
     * @see                   Xmp#setDcRights(java.lang.String)
     */
    public void setCopyrightNotice(String copyrightNotice) {
        this.copyrightNotice.replace(0, this.copyrightNotice.length(),
            copyrightNotice == null
            ? "" // NOI18N
            : copyrightNotice);
    }

    /**
     * Liefert das IPTC-Feld 2:101 (Country/Primary Location Name).
     * 
     * @return IPTC-Feld 2:101 (Country/Primary Location Name) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCountry()
     */
    public String getCountryPrimaryLocationName() {
        return countryPrimaryLocationName.length() <= 0
            ? null : countryPrimaryLocationName.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:101 (Country/Primary Location Name).
     * 
     * @param countryPrimaryLocationName IPTC-Feld 2:101 (Country/Primary Location Name)
     * @see                              Xmp#setPhotoshopCountry(java.lang.String)
     */
    public void setCountryPrimaryLocationName(String countryPrimaryLocationName) {
        this.countryPrimaryLocationName.replace(0,
            this.countryPrimaryLocationName.length(),
            countryPrimaryLocationName == null
            ? "" // NOI18N
            : countryPrimaryLocationName);
    }

    /**
     * Liefert das IPTC-Feld 2:55 (Creation Date).
     * 
     * @return IPTC-Feld 2:55 (Creation Date) oder null, wenn nicht definiert
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Liefert das IPTC-Feld 2:55 (Creation Date).
     * 
     * @param creationDate IPTC-Feld 2:55 (Creation Date)
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Liefert das IPTC-Feld 2:110 (Credit).
     * 
     * @return IPTC-Feld 2:110 (Credit) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCredit()
     */
    public String getCredit() {
        return credit.length() <= 0 ? null : credit.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:110 (Credit).
     * 
     * @param credit IPTC-Feld 2:110 (Credit)
     * @see          Xmp#setPhotoshopCredit(java.lang.String)
     */
    public void setCredit(String credit) {
        this.credit.replace(0, this.credit.length(),
            credit == null
            ? "" // NOI18N
            : credit);
    }

    /**
     * Liefert das IPTC-Feld 2:105 (Headline).
     * 
     * @return IPTC-Feld 2:105 (Headline) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopHeadline()
     */
    public String getHeadline() {
        return headline.length() <= 0 ? null : headline.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:105 (Headline).
     * 
     * @param headline IPTC-Feld 2:105 (Headline)
     * @see            Xmp#setPhotoshopHeadline(java.lang.String)
     */
    public void setHeadline(String headline) {
        this.headline.replace(0, this.headline.length(),
            headline == null
            ? "" // NOI18N
            : headline);
    }

    /**
     * Liefert die IPTC-Felder 2:25 (Keywords).
     * 
     * @return IPTC-Felder 2:25 (Keywords) oder null, wenn nicht definiert
     * @see    Xmp#getDcSubjects()
     */
    public Vector<String> getKeywords() {
        return keywords;
    }

    /**
     * Fügt ein IPTC-Feld 2:25 (Keywords) hinzu.
     * 
     * @param keyword IPTC-Feld 2:25 (Keyword)
     * @see           Xmp#addDcSubject(java.lang.String)
     */
    public void addKeyword(String keyword) {
        if (keyword != null && !keywords.contains(keyword)) {
            keywords.add(keyword);
        }
    }

    /**
     * Liefert das IPTC-Feld 2:05 (Object Name).
     * 
     * @return IPTC-Feld 2:05 (Object Name) oder null, wenn nicht definiert
     * @see    Xmp#getDcTitle()
     */
    public String getObjectName() {
        return objectName.length() <= 0 ? null : objectName.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:05 (Object Name).
     * 
     * @param objectName IPTC-Feld 2:05 (Object Name)
     * @see              Xmp#setDcTitle(java.lang.String)
     */
    public void setObjectName(String objectName) {
        this.objectName.replace(0, this.objectName.length(),
            objectName == null
            ? "" // NOI18N
            : objectName);
    }

    /**
     * Liefert das IPTC-Feld 2:103 (Original Transmission Reference).
     * 
     * @return IPTC-Feld 2:103 (Original Transmission Reference) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopTransmissionReference()
     */
    public String getOriginalTransmissionReference() {
        return originalTransmissionReference.length() <= 0
            ? null : originalTransmissionReference.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:103 (Original Transmission Reference).
     * 
     * @param originalTransmissionReference IPTC-Feld 2:103 (Original Transmission Reference)
     * @see                                 Xmp#setPhotoshopTransmissionReference(java.lang.String)
     */
    public void setOriginalTransmissionReference(
        String originalTransmissionReference) {
        this.originalTransmissionReference.replace(0,
            this.originalTransmissionReference.length(),
            originalTransmissionReference == null
            ? "" // NOI18N
            : originalTransmissionReference);
    }

    /**
     * Liefert das IPTC-Feld 2:95 (Province/State).
     * 
     * @return IPTC-Feld 2:95 (Province/State) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopState()
     */
    public String getProvinceState() {
        return provinceState.length() <= 0 ? null : provinceState.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:95 (Province/State).
     * 
     * @param provinceState IPTC-Feld 2:95 (Province/State)
     * @see                 Xmp#setPhotoshopState(java.lang.String)
     */
    public void setProvinceState(String provinceState) {
        this.provinceState.replace(0, this.provinceState.length(),
            provinceState == null
            ? "" // NOI18N
            : provinceState);
    }

    /**
     * Liefert das IPTC-Feld 2:115 (Source).
     * 
     * @return IPTC-Feld 2:115 (Source) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopSource()
     */
    public String getSource() {
        return source.length() <= 0 ? null : source.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:115 (Source).
     * 
     * @param source IPTC-Feld 2:115 (Source)
     * @see          Xmp#setPhotoshopSource(java.lang.String)
     */
    public void setSource(String source) {
        this.source.replace(0, this.source.length(),
            source == null
            ? "" // NOI18N
            : source);
    }

    /**
     * Liefert das IPTC-Feld 2:40 (Special Instructions).
     * 
     * @return ITPTC-Feld 2:40 (Special Instructions) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopInstructions()
     */
    public String getSpecialInstructions() {
        return specialInstructions.length() <= 0 ? null : specialInstructions.toString();
    }

    /**
     * Setzt das IPTC-Feld 2:40 (Special Instructions).
     * 
     * @param specialInstructions IPTC-Feld 2:40 (Special Instructions)
     * @see                       Xmp#setPhotoshopInstructions(java.lang.String)
     */
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions.replace(0, this.specialInstructions.length(),
            specialInstructions == null
            ? "" // NOI18N
            : specialInstructions);
    }

    /**
     * Liefert die IPTC-Felder 2:20 (Supplemental Category).
     * 
     * @return IPTC-Felder 2:20 (Supplemental Category) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopSupplementalCategories()
     */
    public Vector<String> getSupplementalCategories() {
        return supplementalCategories;
    }

    /**
     * Fügt ein IPTC-Feld 2:20 (Supplemental Category) hinzu.
     * 
     * @param supplementalCategory IPTC-Feld 2:20 (Supplemental Category)
     * @see                        Xmp#addPhotoshopSupplementalCategory(java.lang.String)
     */
    public void addSupplementalCategory(String supplementalCategory) {
        if (supplementalCategory != null &&
            !supplementalCategories.contains(supplementalCategory)) {
            supplementalCategories.add(supplementalCategory);
        }
    }

    /**
     * Liefert die IPTC-Felder 2:122 (Writer/Editor).
     * 
     * @return IPTC-Felder 2:122 (Writer/Editor) oder null, wenn nicht definiert
     * @see    Xmp#getPhotoshopCaptionwriter()
     */
    public Vector<String> getWritersEditors() {
        return writersEditors;
    }

    /**
     * Fügt ein IPTC-Feld 2:122 (Writer/Editor) hinzu.
     * 
     * @param writerEditor IPTC-Feld 2:122 (Writer/Editor)
     * @see                Xmp#setPhotoshopCaptionwriter(java.lang.String)
     */
    public void addWriterEditor(String writerEditor) {
        if (writerEditor != null && !writersEditors.contains(writerEditor)) {
            writersEditors.add(writerEditor);
        }
    }

    /**
     * Liefert den Wert einer IPTC-Spalte.
     * 
     * @param  iptcColumn  IPTC-Spalte
     * @return Wert: Ein String für sich nicht wiederholende Werte oder ein
     *         String-Vector für sich wiederholdende Werte oder null, wenn
     *         für diese Spalte kein Wert gesetzt ist
     */
    public Object getValue(Column iptcColumn) {
        Object value = valueOfColumn.get(iptcColumn);
        if (value instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer) value;
            if (stringBuffer.length() <= 0) {
                return null;
            } else {
                return stringBuffer.toString();
            }
        } else if (value instanceof Vector) {
            Vector vector = (Vector) value;
            if (vector.isEmpty()) {
                return null;
            } else {
                return vector;
            }
        } else if (value instanceof Date) {
            return creationDate;
        }
        return null;
    }

    /**
     * Setzt den Wert einer Spalte. Wiederholt sich der Wert, wird er seinem
     * Array hinzugefügt.
     * 
     * @param iptcColumn  IPTC-Spalte
     * @param value      Wert
     */
    @SuppressWarnings("unchecked")
    public void setValue(Column iptcColumn, String value) {
        Object o = valueOfColumn.get(iptcColumn);
        if (o instanceof StringBuffer) {
            StringBuffer stringBuffer = (StringBuffer) o;
            stringBuffer.replace(0, stringBuffer.length(), value == null
                ? "" // NOI18N
                : value);
        } else if (o instanceof Vector && value != null) {
            Vector vector = (Vector) o;
            if (!vector.contains(value)) {
                vector.add(value);
            }
        }
    }

    /**
     * Liefert, ob keine Daten enthalten sind.
     * 
     * @return true, wenn keine Daten enthalten sind
     */
    public boolean isEmpty() {
        return byLines.isEmpty() &&
            byLinesTitles.isEmpty() &&
            captionAbstract.length() <= 0 &&
            category.length() <= 0 &&
            city.length() <= 0 &&
            contentLocationCodes.isEmpty() &&
            contentLocationNames.isEmpty() &&
            copyrightNotice.length() <= 0 &&
            countryPrimaryLocationName.length() <= 0 &&
            creationDate == null &&
            credit.length() <= 0 &&
            headline.length() <= 0 &&
            keywords.isEmpty() &&
            objectName.length() <= 0 &&
            originalTransmissionReference.length() <= 0 &&
            provinceState.length() <= 0 &&
            source.length() <= 0 &&
            specialInstructions.length() <= 0 &&
            supplementalCategories.isEmpty() &&
            writersEditors.isEmpty();
    }
}
