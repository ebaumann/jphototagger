package org.jphototagger.api.windows;

import javax.swing.Action;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowMenuAction {

    Action getAction();

    /**
     *
     * @return netative value if not important
     */
    int getPosition();

    boolean isSeparatorBefore();
}
