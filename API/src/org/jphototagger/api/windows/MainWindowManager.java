package org.jphototagger.api.windows;

import org.jphototagger.api.messages.MessageType;

/**
 * @author Elmar Baumann
 */
public interface MainWindowManager {

    /**
     * The selection window is left within the application's main window
     * and will be used to select directories, keywords etc.
     *
     * @param appWindow
     */
    void dockIntoSelectionWindow(MainWindowComponent appWindow);

    /**
     * The edit window is right within the application's main window
     * and will be used to edit metadata.
     *
     * @param appWindow
     */
    void dockIntoEditWindow(MainWindowComponent appWindow);

    void setMainWindowTitle(String title);

    void setMainWindowStatusbarText(String text, MessageType type, long millisecondsToDisplay);
}
