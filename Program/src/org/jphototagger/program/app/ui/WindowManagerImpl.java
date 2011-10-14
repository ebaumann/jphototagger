package org.jphototagger.program.app.ui;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowManager.class)
public final class WindowManagerImpl implements MainWindowManager {

    @Override
    public void dockIntoSelectionWindow(final MainWindowComponent appWindow) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getAppPanel().dockIntoSelectionWindow(appWindow);
            }
        });
    }

    @Override
    public void dockIntoEditWindow(final MainWindowComponent appWindow) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getAppPanel().dockIntoEditWindow(appWindow);
            }
        });
    }

    @Override
    public void setMainWindowTitle(final String title) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getAppFrame().setTitle(title);
            }
        });
    }

    @Override
    public void setMainWindowStatusbarText(final String text, final MessageType type, final long millisecondsToDisplay) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getAppPanel().setStatusbarText(text, type, millisecondsToDisplay);
            }
        });
    }
}
