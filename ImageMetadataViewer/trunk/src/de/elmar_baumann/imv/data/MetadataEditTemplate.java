package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Vorlage zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-22
 */
public final class MetadataEditTemplate {
    // Diese Klasse sollte je ein Attribut enthalten für jede Spalte aus
    // de.elmar_baumann.imv.database.metadata.selections.EditColumns
    private final StringBuffer name = new StringBuffer();
    private final StringBuffer dcCreator = new StringBuffer();
    private final StringBuffer dcDescription = new StringBuffer();
    private final StringBuffer dcRights = new StringBuffer();
    private final StringBuffer dcSubjects = new StringBuffer();
    private final StringBuffer dcTitle = new StringBuffer();
    private final StringBuffer iptc4xmpcoreLocation = new StringBuffer();
    private final StringBuffer iptc4xmpcoreCountrycode = new StringBuffer();
    private final StringBuffer photoshopHeadline = new StringBuffer();
    private final StringBuffer photoshopCaptionwriter = new StringBuffer();
    private final StringBuffer photoshopCategory = new StringBuffer();
    private final StringBuffer photoshopSupplementalCategories = new StringBuffer();
    private final StringBuffer photoshopAuthorsposition = new StringBuffer();
    private final StringBuffer photoshopCity = new StringBuffer();
    private final StringBuffer photoshopState = new StringBuffer();
    private final StringBuffer photoshopCountry = new StringBuffer();
    private final StringBuffer photoshopTransmissionReference = new StringBuffer();
    private final StringBuffer photoshopInstructions = new StringBuffer();
    private final StringBuffer photoshopCredit = new StringBuffer();
    private final StringBuffer photoshopSource = new StringBuffer();
    private final Map<Column, StringBuffer> valueOfColumn = new HashMap<Column, StringBuffer>();

    private void initValueOfColumn() {
        valueOfColumn.put(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubjects);
        valueOfColumn.put(ColumnXmpDcTitle.INSTANCE, dcTitle);
        valueOfColumn.put(ColumnXmpPhotoshopHeadline.INSTANCE, photoshopHeadline);
        valueOfColumn.put(ColumnXmpDcDescription.INSTANCE, dcDescription);
        valueOfColumn.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, photoshopCaptionwriter);
        valueOfColumn.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, iptc4xmpcoreLocation);
        valueOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, iptc4xmpcoreCountrycode);
        valueOfColumn.put(ColumnXmpPhotoshopCategory.INSTANCE, photoshopCategory);
        valueOfColumn.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, photoshopSupplementalCategories);
        valueOfColumn.put(ColumnXmpDcRights.INSTANCE, dcRights);
        valueOfColumn.put(ColumnXmpDcCreator.INSTANCE, dcCreator);
        valueOfColumn.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, photoshopAuthorsposition);
        valueOfColumn.put(ColumnXmpPhotoshopCity.INSTANCE, photoshopCity);
        valueOfColumn.put(ColumnXmpPhotoshopState.INSTANCE, photoshopState);
        valueOfColumn.put(ColumnXmpPhotoshopCountry.INSTANCE, photoshopCountry);
        valueOfColumn.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, photoshopTransmissionReference);
        valueOfColumn.put(ColumnXmpPhotoshopInstructions.INSTANCE, photoshopInstructions);
        valueOfColumn.put(ColumnXmpPhotoshopCredit.INSTANCE, photoshopCredit);
        valueOfColumn.put(ColumnXmpPhotoshopSource.INSTANCE, photoshopSource);
    }

    public MetadataEditTemplate() {
        initValueOfColumn();
    }

    public String getDcCreator() {
        return dcCreator.toString();
    }

    public void setDcCreator(String dcCreator) {
        this.dcCreator.replace(0, this.dcCreator.length(), dcCreator);
    }

    public String getDcDescription() {
        return dcDescription.toString();
    }

    public void setDcDescription(String dcDescription) {
        this.dcDescription.replace(0, this.dcDescription.length(), dcDescription);
    }

    public String getDcRights() {
        return dcRights.toString();
    }

    public void setDcRights(String dcRights) {
        this.dcRights.replace(0, this.dcRights.length(), dcRights);
    }

    public String getDcSubjects() {
        return dcSubjects.toString();
    }

    public void setDcSubjects(String dcSubjects) {
        this.dcSubjects.replace(0, this.dcSubjects.length(), dcSubjects);
    }

    public String getDcTitle() {
        return dcTitle.toString();
    }

    public void setDcTitle(String dcTitle) {
        this.dcTitle.replace(0, this.dcTitle.length(), dcTitle);
    }

    public String getIptc4xmpcoreCountrycode() {
        return iptc4xmpcoreCountrycode.toString();
    }

    public void setIptc4xmpcoreCountrycode(String iptc4xmpcoreCountrycode) {
        this.iptc4xmpcoreCountrycode.replace(0, this.iptc4xmpcoreCountrycode.length(), iptc4xmpcoreCountrycode);
    }

    public String getIptc4xmpcoreLocation() {
        return iptc4xmpcoreLocation.toString();
    }

    public void setIptc4xmpcoreLocation(String iptc4xmpcoreLocation) {
        this.iptc4xmpcoreLocation.replace(0, this.iptc4xmpcoreLocation.length(), iptc4xmpcoreLocation);
    }

    /**
     * Liefert den Namen der Sammlung.
     * 
     * @return Name oder null wenn nicht definiert
     */
    public String getName() {
        return name.toString();
    }

    public void setName(String name) {
        this.name.replace(0, this.name.length(), name);
    }

    public String getPhotoshopAuthorsposition() {
        return photoshopAuthorsposition.toString();
    }

    public void setPhotoshopAuthorsposition(String photoshopAuthorsposition) {
        this.photoshopAuthorsposition.replace(0, this.photoshopAuthorsposition.length(), photoshopAuthorsposition);
    }

    public String getPhotoshopCaptionwriter() {
        return photoshopCaptionwriter.toString();
    }

    public void setPhotoshopCaptionwriter(String photoshopCaptionwriter) {
        this.photoshopCaptionwriter.replace(0, this.photoshopCaptionwriter.length(), photoshopCaptionwriter);
    }

    public String getPhotoshopCategory() {
        return photoshopCategory.toString();
    }

    public void setPhotoshopCategory(String photoshopCategory) {
        this.photoshopCategory.replace(0, this.photoshopCategory.length(), photoshopCategory);
    }

    public String getPhotoshopCity() {
        return photoshopCity.toString();
    }

    public void setPhotoshopCity(String photoshopCity) {
        this.photoshopCity.replace(0, this.photoshopCity.length(), photoshopCity);
    }

    public String getPhotoshopCountry() {
        return photoshopCountry.toString();
    }

    public void setPhotoshopCountry(String photoshopCountry) {
        this.photoshopCountry.replace(0, this.photoshopCountry.length(), photoshopCountry);
    }

    public String getPhotoshopCredit() {
        return photoshopCredit.toString();
    }

    public void setPhotoshopCredit(String photoshopCredit) {
        this.photoshopCredit.replace(0, this.photoshopCredit.length(), photoshopCredit);
    }

    public String getPhotoshopHeadline() {
        return photoshopHeadline.toString();
    }

    public void setPhotoshopHeadline(String photoshopHeadline) {
        this.photoshopHeadline.replace(0, this.photoshopHeadline.length(), photoshopHeadline);
    }

    public String getPhotoshopInstructions() {
        return photoshopInstructions.toString();
    }

    public void setPhotoshopInstructions(String photoshopInstructions) {
        this.photoshopInstructions.replace(0, this.photoshopInstructions.length(), photoshopInstructions);
    }

    public String getPhotoshopSource() {
        return photoshopSource.toString();
    }

    public void setPhotoshopSource(String photoshopSource) {
        this.photoshopSource.replace(0, this.photoshopSource.length(), photoshopSource);
    }

    public String getPhotoshopState() {
        return photoshopState.toString();
    }

    public void setPhotoshopState(String photoshopState) {
        this.photoshopState.replace(0, this.photoshopState.length(), photoshopState);
    }

    public String getPhotoshopSupplementalCategories() {
        return photoshopSupplementalCategories.toString();
    }

    public void setPhotoshopSupplementalCategories(String photoshopSupplementalCategories) {
        this.photoshopSupplementalCategories.replace(0, this.photoshopSupplementalCategories.length(), photoshopSupplementalCategories);
    }

    public String getPhotoshopTransmissionReference() {
        return photoshopTransmissionReference.toString();
    }

    public void setPhotoshopTransmissionReference(String photoshopTransmissionReference) {
        this.photoshopTransmissionReference.replace(0, this.photoshopTransmissionReference.length(), photoshopTransmissionReference);
    }

    /**
     * Liefert, ob keine Daten enthalten sind.
     * 
     * @return true, wenn keine Daten enthalten sind
     */
    public boolean isEmpty() {
        return dcSubjects.length() == 0 &&
            dcTitle.length() == 0 &&
            photoshopHeadline.length() == 0 &&
            dcDescription.length() == 0 &&
            photoshopCaptionwriter.length() == 0 &&
            iptc4xmpcoreLocation.length() == 0 &&
            iptc4xmpcoreCountrycode.length() == 0 &&
            photoshopCategory.length() == 0 &&
            photoshopSupplementalCategories.length() == 0 &&
            dcRights.length() == 0 &&
            dcCreator.length() == 0 &&
            photoshopAuthorsposition.length() == 0 &&
            photoshopCity.length() == 0 &&
            photoshopState.length() == 0 &&
            photoshopCountry.length() == 0 &&
            photoshopTransmissionReference.length() == 0 &&
            photoshopInstructions.length() == 0 &&
            photoshopCredit.length() == 0 &&
            photoshopSource.length() == 0;
    }

    /**
     * Liefert, ob ein Name für das Template enthalten ist. Dieser ist
     * Identifikator.
     * 
     * @return true, wenn ein Name vorhanden ist
     */
    public boolean hasName() {
        return name != null && !name.toString().trim().isEmpty();
    }

    /**
     * Liefert den Wert für eine XMP-Spalte.
     * 
     * @param column   Spalte
     * @return Spaltenwert
     */
    public String getValueOfColumn(Column column) {
        StringBuffer value = valueOfColumn.get(column);
        return value == null ? "" : value.toString(); // NOI18N
    }

    /**
     * Setzt den Wert einer XMP-Spalte.
     * 
     * @param column  Spalte
     * @param data    Wert
     */
    public void setValueOfColumn(Column column, String data) {
        StringBuffer value = valueOfColumn.get(column);
        value.replace(0, value.length(), data);
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetadataEditTemplate other = (MetadataEditTemplate) obj;
        if (this.name == null || !this.name.toString().equals(other.name.toString())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
