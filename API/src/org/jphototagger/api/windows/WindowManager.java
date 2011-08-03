package org.jphototagger.api.windows;

import java.awt.Component;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface WindowManager {

    /**
     * The selection window is left used to select directories, keywords etc.
     *
     * @param component
     */
    void dockIntoSelectionWindow(Component component);

    /**
     * The edit window is right used to edit metadata.
     *
     * @param component
     */
    void dockIntoEditWindow(Component component);

    /**
     * The properties window displays detailled information about something.
     *
     * @param component
     */
    void dockIntoPropertiesWindow(Component component);
}
