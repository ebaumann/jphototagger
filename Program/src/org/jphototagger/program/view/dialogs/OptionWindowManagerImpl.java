package org.jphototagger.program.view.dialogs;

import java.awt.Component;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.OptionWindowManager;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionWindowManager.class)
public final class OptionWindowManagerImpl implements OptionWindowManager {

    @Override
    public void addSettingsComponent(final Component component, final String title) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                SettingsDialog.INSTANCE.addTabToMiscSettings(component, title);
            }
        });
    }
}
