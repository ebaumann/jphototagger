package de.elmar_baumann.lib.event;

/**
 * Listens to actions of
 * {@link de.elmar_baumann.lib.dialog.HelpBrowser}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/04
 */
public interface HelpBrowserListener {

    /**
     * An action was performed.
     * 
     * @param action  action
     */
    public void actionPerformed(HelpBrowserAction action);
    
}
