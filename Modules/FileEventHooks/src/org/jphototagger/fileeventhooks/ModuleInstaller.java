package org.jphototagger.fileeventhooks;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.OptionsWindowManager;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module {

    private static final FileEventHooksScriptExecutor EXECUTOR = new FileEventHooksScriptExecutor();

    @Override
    public void init() {
        OptionsWindowManager optionWindowManger = Lookup.getDefault().lookup(OptionsWindowManager.class);
        String title = Bundle.getString(ModuleInstaller.class, "SettingsPanel.Title");
        SettingsPanel settingsPanel = new SettingsPanel();

        optionWindowManger.addSettingsComponent(settingsPanel, title);
    }

    @Override
    public void remove() {
        // ignore
    }
}
