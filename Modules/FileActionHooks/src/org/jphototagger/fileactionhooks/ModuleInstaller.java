package org.jphototagger.fileactionhooks;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.OptionWindowManager;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module {

    private static final FileActionScriptExecutor EXECUTOR = new FileActionScriptExecutor();

    @Override
    public void init() {
        OptionWindowManager optionWindowManger = Lookup.getDefault().lookup(OptionWindowManager.class);
        String title = Bundle.getString(ModuleInstaller.class, "SettingsPanel.Title");
        SettingsPanel settingsPanel = new SettingsPanel();

        optionWindowManger.addSettingsComponent(settingsPanel, title);
    }

    @Override
    public void remove() {
        // ignore
    }
}
