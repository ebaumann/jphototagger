package org.jphototagger.lib.image.metadata.gps.kml;

/**
 * An element in a {@link KMLDocument}.
 *
 * @author Elmar Baumann
 */
public interface KMLElement {

    /**
     * Returns a XML representation of the element.
     *
     * @return XML
     */
    String toXML();

    /**
     * Returns whether this element is a top level element.
     *
     * @return true, if this element is a top level element
     */
    boolean isTopLevelElement();
}
