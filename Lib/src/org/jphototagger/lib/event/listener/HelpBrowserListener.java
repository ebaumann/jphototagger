package org.jphototagger.lib.event.listener;

import org.jphototagger.lib.event.HelpBrowserEvent;

/**
 * Listens to actions of
 * {@link org.jphototagger.lib.dialog.HelpBrowser}.
 *
 * @author Elmar Baumann
 */
public interface HelpBrowserListener {

    /**
     * An action was performed.
     *
     * @param action  action
     */
    void actionPerformed(HelpBrowserEvent action);
}
