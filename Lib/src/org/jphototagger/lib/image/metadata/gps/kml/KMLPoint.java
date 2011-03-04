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
    private final double longitude;
    private final double latitude;
    private final double altitude;

    /**
     * Creates a point.
     *
     * @param longitude longitude from -180 to +180 degrees, positive values are
     *                  East and negative values are West
     * @param latitude  latitude from -90 to +90 degrees, positive values are
     *                  North and negative values are South
     * @throws          IllegalArgumentException on unexpected values
     */
    public KMLPoint(double longitude, double latitude) {
        if ((longitude < -180) || (longitude > 180)) {
            throw new IllegalArgumentException("Illegal longitude: " + longitude);
        }

        if ((latitude < -90) || (latitude > 90)) {
            throw new IllegalArgumentException("Illegal latitude: " + latitude);
        }

        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = VALUE_NO_ALTITUDE;
    }

    /**
     * Creates a point with an altitude.
     *
     * @param longitude longitude from -180 to +180 degrees, positive values are
     *                  East and negative values are West
     * @param latitude  latitude from -90 to +90 degrees, positive values are
     *                  North and negative values are South
     * @param altitude  meters obove sea level greater or equals zero
     * @throws          IllegalArgumentException on unexpected values
     */
    public KMLPoint(double longitude, double latitude, double altitude) {
        if ((longitude < -180) || (longitude > 180)) {
            throw new IllegalArgumentException("Illegal longitude: " + longitude);
        }

        if ((latitude < -90) || (latitude > 90)) {
            throw new IllegalArgumentException("Illegal latitude: " + latitude);
        }

        if (altitude < 0) {
            throw new IllegalArgumentException("Illegal altitude: " + altitude);
        }

        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
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
