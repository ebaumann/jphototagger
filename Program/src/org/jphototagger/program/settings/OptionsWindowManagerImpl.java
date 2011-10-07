package org.jphototagger.program.settings;

import org.jphototagger.program.settings.SettingsDialog;
import java.awt.Component;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.OptionsWindowManager;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionsWindowManager.class)
public final class OptionsWindowManagerImpl implements OptionsWindowManager {

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
