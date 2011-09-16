package org.jphototagger.program.view.frames;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowMenuAction;
import org.jphototagger.api.windows.MainWindowMenuManager;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuManager.class)
public final class MainMenuManagerImpl implements MainWindowMenuManager {

    @Override
    public void addToFileMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToFileMenu(appMenuAction);
    }

    @Override
    public void addToEditMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToEditMenu(appMenuAction);
    }

    @Override
    public void addToViewMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToViewMenu(appMenuAction);
    }

    @Override
    public void addToGotoMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToGotoMenu(appMenuAction);
    }

    @Override
    public void addToToolsMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToToolsMenu(appMenuAction);
    }

    @Override
    public void addToWindowMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToWindowMenu(appMenuAction);
    }

    @Override
    public void addToHelpMenu(MainWindowMenuAction appMenuAction) {
        GUI.getAppFrame().addToHelpMenu(appMenuAction);
    }
}
