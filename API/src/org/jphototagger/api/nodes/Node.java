package org.jphototagger.api.nodes;

import java.util.Collection;

import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.api.component.IconProvider;

/**
 * A GUI representant of data.
 *
 * @author Elmar Baumann
 */
public interface Node extends DisplayNameProvider, IconProvider {

    /**
     *
     * @return represented content, e.g. what maybe of interest for Lookup listeners
     */
    Collection<?> getContent();
}
