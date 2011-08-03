package org.jphototagger.program.view.panels;

import org.jphototagger.api.windows.AppWindow;
import org.jphototagger.api.windows.WindowManager;
import org.jphototagger.program.resource.GUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = WindowManager.class)
public final class WindowManagerImpl implements WindowManager {

    @Override
    public void dockIntoSelectionWindow(AppWindow appWindow) {
        GUI.getAppPanel().dockIntoSelectionWindow(appWindow);
    }

    @Override
    public void dockIntoEditWindow(AppWindow appWindow) {
        GUI.getAppPanel().dockIntoEditWindow(appWindow);
    }
}
