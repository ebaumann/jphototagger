package org.jphototagger.api.windows;

/**
 * @author Elmar Baumann
 */
public interface MainWindowMenuManager {

    void addToFileMenu(MainWindowMenuItem appMenuAction);

    void addToEditMenu(MainWindowMenuItem appMenuAction);

    void addToViewMenu(MainWindowMenuItem appMenuAction);

    void addToGotoMenu(MainWindowMenuItem appMenuAction);

    void addToToolsMenu(MainWindowMenuItem appMenuAction);

    void addToWindowMenu(MainWindowMenuItem appMenuAction);

    void addToHelpMenu(MainWindowMenuItem appMenuAction);
}
