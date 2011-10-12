package org.jphototagger.program.app.ui;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.messages.StatusBarMessageDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusBarMessageDisplayer.class)
public final class StatusBarMessageDisplayerImpl implements StatusBarMessageDisplayer {

    private final AppPanel appPanel = GUI.getAppPanel();

    @Override
    public void setStatusbarText(final String text, final MessageType type, final long millisecondsToDisplay) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                appPanel.setStatusbarText(text, type, millisecondsToDisplay);
            }
        });
    }
}
