package org.jphototagger.api.nodes;

import java.util.Collection;
import javax.swing.Icon;

/**
 * A GUI representant of data.
 *
 * @author Elmar Baumann
 */
public interface Node {

    /**
     *
     * @return what maybe of interest for Lookup listeners, e.g. the represented data
     */
    Collection<?> getLookupContent();

    String getDisplayName();

    String getHtmlDisplayName();

    /**
     *
     * @return maybe null
     */
    Icon getSmallIcon();

    /**
     *
     * @return maybe null
     */
    Icon getLargeIcon();
}
