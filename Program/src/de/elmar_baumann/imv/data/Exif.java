/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.data;

import java.sql.Date;

/**
 * EXIF metadata or an image file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-27
 */
public final class Exif {

    private Date dateTimeOriginal;
    private double focalLength = -1;
    private short isoSpeedRatings = -1;
    private String recordingEquipment;

    /**
     * Returns the date when the image was created.
     * 
     * @return date or null if not defined
     */
    public Date getDateTimeOriginal() {
        return dateTimeOriginal == null
                ? null
                : new Date(dateTimeOriginal.getTime());
    }

    /**
     * Sets the date when the image was created.
     * 
     * @param dateTimeOriginal date
     */
    public void setDateTimeOriginal(Date dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
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
        // Bugfix imagero: when first byte of RAW data is 0, then the returned
        // string is "0"
        this.recordingEquipment = recordingEquipment == null ||
                recordingEquipment.equals("0") // NOI18N
                                  ? null
                                  : recordingEquipment;
    }

    /**
     * Returns wheter no EXIF field of this class was set.
     * 
     * @return true if no EXIF field was set
     */
    public boolean isEmpty() {
        return dateTimeOriginal == null &&
                focalLength < 0 &&
                isoSpeedRatings < 0 &&
                recordingEquipment == null;
    }
}
