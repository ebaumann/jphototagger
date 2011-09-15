package org.jphototagger.api.windows;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowManager {

    /**
     * The selection window is left used to select directories, keywords etc.
     *
     * @param appWindow
     */
    void dockIntoSelectionWindow(MainWindowComponent appWindow);

    /**
     * The edit window is right used to edit metadata.
     *
     * @param appWindow
     */
    void dockIntoEditWindow(MainWindowComponent appWindow);
}
