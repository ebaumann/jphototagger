package org.jphototagger.program.data;

import java.sql.Date;

import java.text.SimpleDateFormat;

/**
 * EXIF metadata or an image file.
 *
 * @author Elmar Baumann
 */
public final class Exif {
    private Date dateTimeOriginal;
    private double focalLength = -1;
    private short isoSpeedRatings = -1;
    private String recordingEquipment;
    private String lens;

    /**
     * Returns the date when the image was created.
     *
     * @return date or null if not defined
     */
    public Date getDateTimeOriginal() {
        return (dateTimeOriginal == null)
               ? null
               : new Date(dateTimeOriginal.getTime());
    }

    /**
     * Sets the date when the image was created.
     *
     * @param dateTimeOriginal date
     */
    public void setDateTimeOriginal(Date dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal == null
                ? null
                : new Date(dateTimeOriginal.getTime());
    }

    /**
     * Returns the focal length of the camera's lens which projected the image.
     *
     * @return focal length in mm
     */
    public double getFocalLength() {
        return focalLength;
    }

    /**
     * Sets the focal length of the camera's lens which projected the image.
     *
     * @param focalLength focal length in mm
     */
    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    /**
     * Returns the ISO adjustment of the camera which took the image.
     *
     * @return ISO
     */
    public short getIsoSpeedRatings() {
        return isoSpeedRatings;
    }

    /**
     * Sets the ISO adjustment of the camera which took the image
     *
     * @param isoSpeedRatings ISO
     */
    public void setIsoSpeedRatings(short isoSpeedRatings) {
        this.isoSpeedRatings = isoSpeedRatings;
    }

    /**
     * Returns the camera which took the image.
     *
     * @return camera
     */
    public String getRecordingEquipment() {
        return recordingEquipment;
    }

    /**
     * Sets the camera which took the image.
     *
     * @param recordingEquipment camera
     */
    public void setRecordingEquipment(String recordingEquipment) {

        // Bugfix imagero: If first byte of RAW data is 0, then the returned string is "0"
        this.recordingEquipment = ((recordingEquipment == null) || recordingEquipment.equals("0"))
                                  ? null
                                  : recordingEquipment;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getXmpDateCreated() {
        if (dateTimeOriginal == null) {
            return "";
        }

        return new SimpleDateFormat("yyyy-MM-dd").format(dateTimeOriginal);
    }

    public boolean isEmpty() {
        return (dateTimeOriginal == null) && (focalLength < 0) && (isoSpeedRatings < 0) && (recordingEquipment == null);
    }
}
