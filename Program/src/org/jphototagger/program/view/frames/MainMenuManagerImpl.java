package org.jphototagger.program.view.frames;

import org.jphototagger.api.windows.AppMenuAction;
import org.jphototagger.api.windows.MainMenuManger;
import org.jphototagger.program.resource.GUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainMenuManger.class)
public final class MainMenuManagerImpl implements MainMenuManger {

    @Override
    public void addToFileMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToFileMenu(appMenuAction);
    }

    @Override
    public void addToEditMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToEditMenu(appMenuAction);
    }

    @Override
    public void addToViewMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToViewMenu(appMenuAction);
    }

    @Override
    public void addToGotoMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToGotoMenu(appMenuAction);
    }

    @Override
    public void addToToolsMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToToolsMenu(appMenuAction);
    }

    @Override
    public void addToWindowMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToWindowMenu(appMenuAction);
    }

    @Override
    public void addToHelpMenu(AppMenuAction appMenuAction) {
        GUI.getAppFrame().addToHelpMenu(appMenuAction);
    }
}
