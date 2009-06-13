package de.elmar_baumann.imv.data;

import java.sql.Date;

/**
 * EXIF-Daten einer Bilddatei.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class Exif {

    private Date dateTimeOriginal;
    private double focalLength = -1;
    private short isoSpeedRatings = -1;
    private String recordingEquipment;

    /**
     * Liefert die Aufnahmezeit.
     * 
     * @return Aufnahmezeit
     */
    public Date getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    /**
     * Setzt die Aufnahmezeit.
     * 
     * @param dateTimeOriginal Aufnahmezeit
     */
    public void setDateTimeOriginal(Date dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
    }

    /**
     * Liefert die Brennweite.
     * 
     * @return Brennweite
     */
    public double getFocalLength() {
        return focalLength;
    }

    /**
     * Setzt die Brennweite.
     * 
     * @param focalLength Brennweite
     */
    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    /**
     * Liefert die ISO-Einstellung.
     * 
     * @return ISO-Einstellung
     */
    public short getIsoSpeedRatings() {
        return isoSpeedRatings;
    }

    /**
     * Setzt die ISO-Einstellung.
     * 
     * @param isoSpeedRatings ISO-Einstellung
     */
    public void setIsoSpeedRatings(short isoSpeedRatings) {
        this.isoSpeedRatings = isoSpeedRatings;
    }

    /**
     * Liefert die Kamera.
     * 
     * @return Kamera
     */
    public String getRecordingEquipment() {
        return recordingEquipment;
    }

    /**
     * Setzt die Kamera.
     * 
     * @param recordingEquipment Kamera
     */
    public void setRecordingEquipment(String recordingEquipment) {
        // Bugfix imagero: when first byte of RAW data is 0, then the returned
        // string is "0"
        if (recordingEquipment != null && !recordingEquipment.equals("0")) {
            this.recordingEquipment = recordingEquipment;
        }
    }

    /**
     * Liefert, ob keine Attribute existitieren (nichts definiert ist).
     * 
     * @return true, wenn keine Attribute existieren
     */
    public boolean isEmpty() {
        return dateTimeOriginal == null &&
                focalLength < 0 &&
                isoSpeedRatings < 0 &&
                recordingEquipment == null;
    }
}
