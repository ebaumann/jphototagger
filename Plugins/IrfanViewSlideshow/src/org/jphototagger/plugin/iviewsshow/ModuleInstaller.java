package org.jphototagger.plugin.iviewsshow;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;

/**
 * @author  Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module, ModuleDescription {

    @Override
    public void init() {
        // Do nothing
    }

    @Override
    public void remove() {
        if (SystemUtil.isWindows()) {
            TemporaryStorage.INSTANCE.cleanup();
        }
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
