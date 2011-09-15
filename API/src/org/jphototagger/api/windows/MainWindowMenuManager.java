package org.jphototagger.api.windows;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowMenuManager {

    void addToFileMenu(MainWindowMenuAction appMenuAction);

    void addToEditMenu(MainWindowMenuAction appMenuAction);

    void addToViewMenu(MainWindowMenuAction appMenuAction);

    void addToGotoMenu(MainWindowMenuAction appMenuAction);

    void addToToolsMenu(MainWindowMenuAction appMenuAction);

    void addToWindowMenu(MainWindowMenuAction appMenuAction);

    void addToHelpMenu(MainWindowMenuAction appMenuAction);
}
