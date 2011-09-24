package org.jphototagger.program.view.dialogs;

import java.awt.Component;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.OptionWindowManager;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionWindowManager.class)
public final class OptionWindowManagerImpl implements OptionWindowManager {

    @Override
    public void addSettingsComponent(Component component, String title) {
        SettingsDialog.INSTANCE.addTabToMiscSettings(component, title);
    }
}
