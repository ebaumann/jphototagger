package org.jphototagger.program.app.ui;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowManager.class)
public final class WindowManagerImpl implements MainWindowManager {

    @Override
    public void dockIntoSelectionWindow(MainWindowComponent appWindow) {
        GUI.getAppPanel().dockIntoSelectionWindow(appWindow);
    }

    @Override
    public void dockIntoEditWindow(MainWindowComponent appWindow) {
        GUI.getAppPanel().dockIntoEditWindow(appWindow);
    }
}
