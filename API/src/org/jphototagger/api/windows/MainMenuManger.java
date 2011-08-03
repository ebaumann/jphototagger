package org.jphototagger.api.windows;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainMenuManger {

    void addToFileMenu(AppMenuAction appMenuAction);

    void addToEditMenu(AppMenuAction appMenuAction);

    void addToViewMenu(AppMenuAction appMenuAction);

    void addToGotoMenu(AppMenuAction appMenuAction);

    void addToToolsMenu(AppMenuAction appMenuAction);

    void addToWindowMenu(AppMenuAction appMenuAction);

    void addToHelpMenu(AppMenuAction appMenuAction);
}
