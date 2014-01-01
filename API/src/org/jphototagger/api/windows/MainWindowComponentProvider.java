package org.jphototagger.api.windows;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface MainWindowComponentProvider {

    /**
     * The selection window is left within the application's main window
     * and will be used to select directories, keywords etc.
     * @return
     */
    Collection<? extends MainWindowComponent> getMainWindowSelectionComponents();

    /**
     * The edit window is right within the application's main window
     * and will be used to edit metadata.
     * @return 
     */
    Collection<? extends MainWindowComponent> getMainWindowEditComponents();
}
