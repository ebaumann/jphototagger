package org.jphototagger.domain.metadata.exif;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlTransient;

import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class Exif {

    private Date dateTimeOriginal;
    private double focalLength = -1;
    private short isoSpeedRatings = -1;
    private String recordingEquipment;
    private String lens;
    private long dateTimeOriginalTimestamp = -1;
    @XmlTransient
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * @return date when the image was created or null
     */
    public Date getDateTimeOriginal() {
        return (dateTimeOriginal == null)
                ? null
                : new Date(dateTimeOriginal.getTime());
    }

    /**
     * @param dateTimeOriginal date when the image was created or null
     */
    public void setDateTimeOriginal(Date dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal == null
                ? null
                : new Date(dateTimeOriginal.getTime());
    }

    /**
     * @param timestamp time when the image was created in milliseconds since 1970/01/01 00:00:00 or negative value
     */
    public void setDateTimeOriginalTimestamp(long timestamp) {
        dateTimeOriginalTimestamp = timestamp;
    }

    /**
     * @return time when the image was created in milliseconds since 1970/01/01 00:00:00 or negative value
     */
    public long getDateTimeOriginalTimestamp() {
        return dateTimeOriginalTimestamp;
    }

    /**
     * @return focal length in mm
     */
    public double getFocalLength() {
        return focalLength;
    }

    public Double getFocalLengthGreaterZeroOrNull() {
        return focalLength > 0
                ? focalLength
                : null;
    }

    /**
     * @param focalLength focal length in mm
     */
    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    /**
     * @return ISO setting
     */
    public short getIsoSpeedRatings() {
        return isoSpeedRatings;
    }

    /**
     * @param isoSpeedRatings ISO setting
     */
    public void setIsoSpeedRatings(short isoSpeedRatings) {
        this.isoSpeedRatings = isoSpeedRatings;
    }

    /**
     * @return camera
     */
    public String getRecordingEquipment() {
        return recordingEquipment;
    }

    /**
     * @param recordingEquipment camera
     */
    public void setRecordingEquipment(String recordingEquipment) {
        // Bugfix imagero: If first byte of RAW data is 0, then the returned string is "0"
        this.recordingEquipment = recordingEquipment == null || recordingEquipment.equals("0")
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

        return DATE_FORMAT.format(dateTimeOriginal);
    }

    public boolean isEmpty() {
        return dateTimeOriginal == null
                && focalLength < 0
                && isoSpeedRatings < 0
                && dateTimeOriginalTimestamp < 0
                && !StringUtil.hasContent(lens)
                && !StringUtil.hasContent(recordingEquipment);
    }
}
