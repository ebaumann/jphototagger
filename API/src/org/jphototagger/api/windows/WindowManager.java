package org.jphototagger.api.windows;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface WindowManager {

    /**
     * The selection window is left used to select directories, keywords etc.
     *
     * @param appWindow
     */
    void dockIntoSelectionWindow(AppWindow appWindow);

    /**
     * The edit window is right used to edit metadata.
     *
     * @param appWindow
     */
    void dockIntoEditWindow(AppWindow appWindow);
}
