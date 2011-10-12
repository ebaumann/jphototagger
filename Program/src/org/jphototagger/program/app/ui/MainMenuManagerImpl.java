package org.jphototagger.program.app.ui;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowMenuItem;
import org.jphototagger.api.windows.MainWindowMenuManager;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuManager.class)
public final class MainMenuManagerImpl implements MainWindowMenuManager {

    @Override
    public void addToFileMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToFileMenu(appMenuAction);
    }

    @Override
    public void addToEditMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToEditMenu(appMenuAction);
    }

    @Override
    public void addToViewMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToViewMenu(appMenuAction);
    }

    @Override
    public void addToGotoMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToGotoMenu(appMenuAction);
    }

    @Override
    public void addToToolsMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToToolsMenu(appMenuAction);
    }

    @Override
    public void addToWindowMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToWindowMenu(appMenuAction);
    }

    @Override
    public void addToHelpMenu(MainWindowMenuItem appMenuAction) {
        GUI.getAppFrame().addToHelpMenu(appMenuAction);
    }
}
