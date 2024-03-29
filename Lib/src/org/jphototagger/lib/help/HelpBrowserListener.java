package org.jphototagger.lib.help;

/**
 * Listens to actions of
 * {@code org.jphototagger.lib.dialog.HelpBrowser}.
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
