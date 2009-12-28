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
package de.elmar_baumann.jpt.image.metadata.exif.entry;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;

/**
 * GPS information in the EXIF metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
 */
public final class ExifGpsMetadata {

    /**
     * Status of the GPS receiver when the image is recorded
     */
    public enum GpsStatus {

        MEASUREMENT_IS_IN_PROGRESS,
        MEASUREMENT_IS_INTEROPERABILITY
    }

    /**
     * GPS measurement mode
     */
    public enum GpsMeasureMode {

        TWO_DIMENSIONAL,
        THREE_DIMENSIONAL
    }

    /**
     * Unit used to express the GPS receiver speed of movement
     */
    public enum GpsSpeedRef {

        KILOMETERS_PER_HOUR,
        MILES_PER_HOUR,
        KNOTS
    }

    /**
     * Reference for giving the direction of GPS receiver movement
     */
    public enum GpsTrackRef {

        TRUE_DIRECTION,
        MAGNETIC_DIRECTION
    }

    /**
     * Reference for giving the direction of the image when it is
     * captured
     */
    public enum GpsImgDirectionRef {

        TRUE_DIRECTION,
        MAGNETIC_DIRECTION
    }

    /**
     * Reference used for giving the bearing to the destination point
     */
    public enum GpsDestBearingRef {

        TRUE_DIRECTION,
        MAGNETIC_DIRECTION
    }

    /**
     * Unit used to express the distance to the destination point
     */
    public enum GpsDestDistanceRef {

        KILOMETERS,
        MILES,
        KNOTS
    }
    private ExifGpsVersion     version;
    private ExifGpsLatitude    latitude;
    private ExifGpsLongitude   longitude;
    private ExifGpsAltitude    altitude;
    private ExifGpsTimeStamp   timeStamp;
    private String             gpsSatellites;
    private GpsStatus          gpsReceiverStatus;
    private GpsMeasureMode     gpsMeasurementMode;
    private ExifRational       gpsDop;
    private GpsSpeedRef        gpsSpeedRef;
    private ExifRational       gpsSpeed;
    private GpsTrackRef        gpsTrackRef;
    private ExifRational       gpsTrack;
    private GpsImgDirectionRef gpsImgDirectionRef;
    private ExifRational       gpsImgDirection;
    private String             gpsMapDatum;
    private ExifGpsLatitude    destLatitude;
    private ExifGpsLongitude   destLongitude;
    private GpsDestBearingRef  gpsDestBearingRef;
    private ExifRational       gpsDestBearing;
    private GpsDestDistanceRef gpsDestDistanceRef;
    private ExifRational       gpsDistanceToDestination;
    private String             gpsProcessingMethod;
    private String             gpsAreaInformation;
    private String             gpsDateStamp;
    private int                gpsDifferential;

    /**
     * Returns the altitude.
     *
     * @return altitude
     */
    public ExifGpsAltitude altitude() {
        return altitude;
    }

    /**
     * Sets the altitude.
     *
     * @param altitude altitude
     */
    public void setAltitude(ExifGpsAltitude altitude) {
        this.altitude = altitude;
    }

    /**
     * Returns the latitude of the destination point.
     *
     * @return latitude
     */
    public ExifGpsLatitude destLatitude() {
        return destLatitude;
    }

    /**
     * Sets the latitude of the destination point.
     *
     * @param destLatitude latitude
     */
    public void setDestLatitude(ExifGpsLatitude destLatitude) {
        this.destLatitude = destLatitude;
    }

    /**
     * Returns the longitude of the destination point.
     *
     * @return longitude
     */
    public ExifGpsLongitude destLongitude() {
        return destLongitude;
    }

    /**
     * Sets the longitude of the destination point.
     *
     * @param destLongitude longitude
     */
    public void setDestLongitude(ExifGpsLongitude destLongitude) {
        this.destLongitude = destLongitude;
    }

    /**
     * Returns the name of GPS area.
     * 
     * @return name of GPS area
     */
    public String gpsAreaInformation() {
        return gpsAreaInformation;
    }

    /**
     * Sets the name of GPS area.
     *
     * @param gpsAreaInformation
     */
    public void setGpsAreaInformation(String gpsAreaInformation) {
        this.gpsAreaInformation = gpsAreaInformation;
    }

    /**
     * Returns the GPS date.
     *
     * @return date
     */
    public String gpsDateStamp() {
        return gpsDateStamp;
    }

    /**
     * Sets the GPS date.
     *
     * @param gpsDateStamp date
     */
    public void setGpsDateStamp(String gpsDateStamp) {
        this.gpsDateStamp = gpsDateStamp;
    }

    /**
     * Returns the bearing to the destination point. The range of values is
     * from 0.00 to 359.99.
     *
     * @return bearing
     */
    public ExifRational gpsDestBearing() {
        return gpsDestBearing;
    }

    /**
     * Sets the bearing to the destination point. The range of values is
     * from 0.00 to 359.99.
     *
     * @param gpsDestBearing bearing
     */
    public void setGpsDestBearing(ExifRational gpsDestBearing) {
        this.gpsDestBearing = gpsDestBearing;
    }

    /**
     * Returns the reference used for giving the bearing to the destination
     * point.
     *
     * @return reference
     */
    public GpsDestBearingRef gpsDestBearingRef() {
        return gpsDestBearingRef;
    }

    /**
     * Sets the the reference used for giving the bearing to the destination
     * point.
     *
     * @param gpsDestBearingRef reference
     */
    public void setGpsDestBearingRef(GpsDestBearingRef gpsDestBearingRef) {
        this.gpsDestBearingRef = gpsDestBearingRef;
    }

    /**
     * Returns the unit used to express the distance to the destination point.
     *
     * @return unit
     */
    public GpsDestDistanceRef gpsDestDistanceRef() {
        return gpsDestDistanceRef;
    }

    /**
     * Sets the unit used to express the distance to the destination point.
     *
     * @param gpsDestDistanceRef unit
     */
    public void setGpsDestDistanceRef(GpsDestDistanceRef gpsDestDistanceRef) {
        this.gpsDestDistanceRef = gpsDestDistanceRef;
    }

    /**
     * Returns the GPS differential correction.
     *
     * @return differential correction
     */
    public int gpsDifferential() {
        return gpsDifferential;
    }

    /**
     * Sets the GPS differential correction.
     *
     * @param gpsDifferential differential correction
     */
    public void setGpsDifferential(int gpsDifferential) {
        this.gpsDifferential = gpsDifferential;
    }

    /**
     * Returns the distance to destination.
     *
     * @return distance to destination
     */
    public ExifRational gpsDistanceToDestination() {
        return gpsDistanceToDestination;
    }

    /**
     * Sets the distance to destination.
     *
     * @param gpsDistanceToDestination distance to destination
     */
    public void setGpsDistanceToDestination(ExifRational gpsDistanceToDestination) {
        this.gpsDistanceToDestination = gpsDistanceToDestination;
    }

    /**
     * Returns the GPS DOP (data degree of precision). An HDOP value is written
     * during two-dimensional measurement, and PDOP during three-dimensional
     * measurement.
     *
     * @return GPS DOP
     */
    public ExifRational gpsDop() {
        return gpsDop;
    }

    /**
     * Sets the GPS DOP (data degree of precision). An HDOP value is written
     * during two-dimensional measurement, and PDOP during three-dimensional
     * measurement.
     *
     * @param gpsDop GPS DOP
     */
    public void setGpsDop(ExifRational gpsDop) {
        this.gpsDop = gpsDop;
    }

    /**
     * Returns the direction of the image when it was captured. The range of
     * values is from 0.00 to 359.99.
     *
     * @return direction
     */
    public ExifRational gpsImgDirection() {
        return gpsImgDirection;
    }

    /**
     * Sets the direction of the image when it was captured. The range of
     * values is from 0.00 to 359.99.
     *
     * @param gpsImgDirection direction
     */
    public void setGpsImgDirection(ExifRational gpsImgDirection) {
        this.gpsImgDirection = gpsImgDirection;
    }

    /**
     * Returns the reference for giving the direction of the image when it is
     * captured.
     *
     * @return reference
     */
    public GpsImgDirectionRef gpsImgDirectionRef() {
        return gpsImgDirectionRef;
    }

    /**
     * Sets the reference for giving the direction of the image when it is
     * captured.
     *
     * @param gpsImgDirectionRef reference
     */
    public void setGpsImgDirectionRef(GpsImgDirectionRef gpsImgDirectionRef) {
        this.gpsImgDirectionRef = gpsImgDirectionRef;
    }

    /**
     * Returns the geodetic survey data used by the GPS receiver. If the
     * survey data is restricted to Japan, the value of this tag is 'TOKYO'
     * or 'WGS-84'. If a GPS Info tag is recorded, it is strongly recommended
     * that this tag be recorded.
     *
     * @return data
     */
    public String gpsMapDatum() {
        return gpsMapDatum;
    }

    /**
     * Sets the geodetic survey data used by the GPS receiver. If the
     * survey data is restricted to Japan, the value of this tag is 'TOKYO'
     * or 'WGS-84'. If a GPS Info tag is recorded, it is strongly recommended
     * that this tag be recorded.
     *
     * @param gpsMapDatum data
     */
    public void setGpsMapDatum(String gpsMapDatum) {
        this.gpsMapDatum = gpsMapDatum;
    }

    /**
     * Returns the GPS measurement mode.
     *
     * @return GPS measurement mode
     */
    public GpsMeasureMode gpsMeasurementMode() {
        return gpsMeasurementMode;
    }

    /**
     * Sets the GPS measurement mode.
     *
     * @param gpsMeasurementMode GPS measurement mode
     */
    public void setGpsMeasurementMode(GpsMeasureMode gpsMeasurementMode) {
        this.gpsMeasurementMode = gpsMeasurementMode;
    }

    /**
     * Returns the name of GPS processing method.
     *
     * @return name
     */
    public String gpsProcessingMethod() {
        return gpsProcessingMethod;
    }

    /**
     * Sets the name of GPS processing method.
     *
     * @param gpsProcessingMethod name
     */
    public void setGpsProcessingMethod(String gpsProcessingMethod) {
        this.gpsProcessingMethod = gpsProcessingMethod;
    }

    /**
     * Returns the status of the GPS receiver when the image is recorded.
     *
     * @return status
     */
    public GpsStatus gpsReceiverStatus() {
        return gpsReceiverStatus;
    }

    /**
     * Sets the status of the GPS receiver when the image is recorded.
     *
     * @param gpsReceiverStatus status
     */
    public void setGpsReceiverStatus(GpsStatus gpsReceiverStatus) {
        this.gpsReceiverStatus = gpsReceiverStatus;
    }

    /**
     * Returns the GPS satellites used for measurements. This tag can be used
     * to describe the number of satellites, their ID number, angle of elevation,
     * azimuth, SNR and other information in ASCII notation.
     *
     * @return satellites
     */
    public String gpsSatellites() {
        return gpsSatellites;
    }

    /**
     * Sets the GPS satellites used for measurements. This tag can be used
     * to describe the number of satellites, their ID number, angle of elevation,
     * azimuth, SNR and other information in ASCII notation.
     *
     * @param gpsSatellites satellites
     */
    public void setGpsSatellites(String gpsSatellites) {
        this.gpsSatellites = gpsSatellites;
    }

    /**
     * Returns the the speed of GPS receiver movement.
     *
     * @return speed of GPS receiver movement
     */
    public ExifRational gpsSpeed() {
        return gpsSpeed;
    }

    /**
     * Sets the speed of GPS receiver movement.
     *
     * @param gpsSpeed speed of GPS receiver movement
     */
    public void setGpsSpeed(ExifRational gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    /**
     * Returns the unit used to express the GPS receiver speed of movement.
     *
     * @return unit
     */
    public GpsSpeedRef gpsSpeedRef() {
        return gpsSpeedRef;
    }

    /**
     * Sets the unit used to express the GPS receiver speed of movement.
     *
     * @param gpsSpeedRef unit
     */
    public void setGpsSpeedRef(GpsSpeedRef gpsSpeedRef) {
        this.gpsSpeedRef = gpsSpeedRef;
    }

    /**
     * Returns the direction of GPS receiver movement. The range of values is
     * from 0.00 to 359.99.
     *
     * @return direction
     */
    public ExifRational gpsTrack() {
        return gpsTrack;
    }

    /**
     * Sets the direction of GPS receiver movement. The range of values is
     * from 0.00 to 359.99.
     *
     * @param gpsTrack direction
     */
    public void setGpsTrack(ExifRational gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    /**
     * Returns the the reference for giving the direction of GPS receiver
     * movement.
     *
     * @return reference
     */
    public GpsTrackRef gpsTrackRef() {
        return gpsTrackRef;
    }

    /**
     * Sets the reference for giving the direction of GPS receiver movement.
     *
     * @param gpsTrackRef reference
     */
    public void setGpsTrackRef(GpsTrackRef gpsTrackRef) {
        this.gpsTrackRef = gpsTrackRef;
    }

    /**
     * Returns the latitude.
     *
     * @return latitude
     */
    public ExifGpsLatitude latitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude latitude
     */
    public void setLatitude(ExifGpsLatitude latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude.
     *
     * @return longitude
     */
    public ExifGpsLongitude longitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude longitude
     */
    public void setLongitude(ExifGpsLongitude longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the GPS time (atomic clock).
     *
     * @return time
     */
    public ExifGpsTimeStamp timeStamp() {
        return timeStamp;
    }

    /**
     * Sets the GPS time (atomic clock).
     * @param timeStamp time
     */
    public void setTimeStamp(ExifGpsTimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Returns the GPS tag version.
     *
     * @return GPS tag version
     */
    public ExifGpsVersion version() {
        return version;
    }

    /**
     * Sets the GPS tag version.
     *
     * @param version GPS tag version
     */
    public void setVersion(ExifGpsVersion version) {
        this.version = version;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
