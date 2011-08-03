package org.jphototagger.api.windows;

import javax.swing.Action;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface AppMenuAction {

    Action getAction();

    /**
     *
     * @return netative value f not present
     */
    int getPosition();
}
