package org.jphototagger.api.nodes;

import java.util.Collection;

import javax.swing.Action;
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

    /**
     *
     * @return actions or empty list
     */
    Collection<? extends Action> getActions();

    /**
     * "Temporary selections" are selections additional to "regular selections", usually
     * triggered if the user right clicks on a node. So the selection set via left click
     * must not disappear (replaced through a new selection triggered by right click).
     *
     * @return actions or empty list
     */
    Collection<? extends Action> getActionsForTemporarySelections();

    /**
     *
     * @return maybe null
     */
    Action getPreferredAction();
}
