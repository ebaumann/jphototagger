package org.jphototagger.fileeventhooks;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module, ModuleDescription {

    private final FileEventHooksScriptExecutor EXECUTOR = new FileEventHooksScriptExecutor(); // Keep alive

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        // ignore
    }

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }

    @Override
    public String getLocalizedDescription() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Description");
    }
}
