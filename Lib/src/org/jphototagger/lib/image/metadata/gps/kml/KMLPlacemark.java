package org.jphototagger.lib.image.metadata.gps.kml;

/**
 * A placemark within a {@link KMLDocument}.
 * <p>
 * Doc: http://code.google.com/intl/de/apis/kml/documentation/kmlreference.html
 *
 * @author Elmar Baumann
 */
public final class KMLPlacemark implements KMLElement {
    private final KMLPoint point;
    private String name;

    public KMLPlacemark(KMLPoint point) {
        if (point == null) {
            throw new NullPointerException("point == null");
        }

        this.point = point;
    }

    public KMLPoint getPoint() {
        return point;
    }

    /**
     * Returns the name of this placemark.
     *
     * @return name or null (default) if this placemark does not have a name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this placemark.
     *
     * @param name name or null (default) if this placemark does not have a name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the XML representation of a placemark.
     *
     * @return <code>"&lt;Placemark&gt;[&lt;name&gt;Name&lt;/name&gt;]point&lt;/Placemark&gt;"</code>
     *         where point is {@link KMLPoint#toXML()}
     */
    @Override
    public String toXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<Placemark>");
        appendName(sb);
        sb.append(point.toXML());
        sb.append("</Placemark>");

        return sb.toString();
    }

    @Override
    public boolean isTopLevelElement() {
        return true;
    }

    private void appendName(StringBuilder sb) {
        if ((name != null) &&!name.trim().isEmpty()) {
            sb.append("<name>");
            sb.append(name.trim());
            sb.append("</name>");
        }
    }
}
