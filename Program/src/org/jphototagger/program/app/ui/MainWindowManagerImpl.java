package org.jphototagger.program.app.ui;

import java.awt.Component;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowManager.class)
public final class MainWindowManagerImpl implements MainWindowManager {

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

    @Override
    public boolean isSelectionComponentSelected(Component component) {
        return GUI.getAppPanel().isSelectionComponentSelected(component);
    }

    @Override
    public boolean isEditComponentSelected(Component component) {
        return GUI.getAppPanel().isEditComponentSelected(component);
    }
}
