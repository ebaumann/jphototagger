/*
 * @(#)KMLPoint.java    Created on 2010-08-20
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.image.metadata.gps.kml;

/**
 * A point within a {@link KMLDocument}.
 * <p>
 * Doc: http://code.google.com/intl/de/apis/kml/documentation/kmlreference.html
 *
 * @author Elmar Baumann
 */
public final class KMLPoint implements KMLElement {
    private static final double VALUE_NO_ALTITUDE = Double.MIN_VALUE;
    private final double        longitude;
    private final double        latitude;
    private final double        altitude;

    /**
     * Creates a point.
     *
     * @param longitude longitude from -180 to +180 degrees from west (negative)
     *                  to east (positive)
     * @param latitude  latitude from -90 to +90 degrees from north (negative)
     *                  to south (positive)
     * @throws          IllegalArgumentException on unexpected values
     */
    public KMLPoint(double longitude, double latitude) {
        if ((longitude < -180) || (longitude > 180)) {
            throw new IllegalArgumentException("Illegal longitude: "
                                               + longitude);
        }

        if ((latitude < -90) || (latitude > 90)) {
            throw new IllegalArgumentException("Illegal latitude: " + latitude);
        }

        this.longitude = longitude;
        this.latitude  = latitude;
        this.altitude  = VALUE_NO_ALTITUDE;
    }

    /**
     * Creates a point with an altitude.
     *
     * @param longitude longitude from -180 to +180 degrees from west (negative)
     *                  to east (positive)
     * @param latitude  latitude from -90 to +90 degrees from north (negative)
     *                  to south (positive)
     * @param altitude  meters obove sea level greater or equals zero
     * @throws          IllegalArgumentException on unexpected values
     */
    public KMLPoint(double longitude, double latitude, double altitude) {
        if ((longitude < -180) || (longitude > 180)) {
            throw new IllegalArgumentException("Illegal longitude: "
                                               + longitude);
        }

        if ((latitude < -90) || (latitude > 90)) {
            throw new IllegalArgumentException("Illegal latitude: " + latitude);
        }

        if (altitude < 0) {
            throw new IllegalArgumentException("Illegal altitude: " + altitude);
        }

        this.longitude = longitude;
        this.latitude  = latitude;
        this.altitude  = altitude;
    }

    /**
     * Returns the altitude if this point contains an altitude.
     *
     * @return altitude
     * @throws IllegalStateException if {@link #hasAltitude()} is false
     */
    public double getAltitude() {
        if (!hasAltitude()) {
            throw new IllegalStateException("No altitude has been set!");
        }

        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean hasAltitude() {
        return altitude != VALUE_NO_ALTITUDE;
    }

    /**
     * Returns a XML formatted point.
     *
     * @return <code>"&lt;Point&gt;&lt;coordinates&gt;longitude,latitude[,altitude]&lt;/coordinates&gt;&lt;/Point&gt;"</code>
     *         where longitude, latatitude and altitude are numbers with the
     *         format <code>[-][0-9]+.[0-9]+</code>
     */
    @Override
    public String toXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<Point><coordinates>");
        sb.append(longitude);
        sb.append(",");
        sb.append(latitude);
        appendAltitude(sb);
        sb.append("</coordinates></Point>");

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(longitude);
        sb.append(",");
        sb.append(latitude);
        appendAltitude(sb);

        return sb.toString();
    }

    @Override
    public boolean isTopLevelElement() {
        return false;
    }

    private void appendAltitude(StringBuilder sb) {
        if (hasAltitude()) {
            sb.append(",");
            sb.append(altitude);
        }
    }
}
