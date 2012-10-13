package org.jphototagger.userdefinedfiletypes;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module, ModuleDescription {

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        // ignore
    }

    @Override
    public String getLocalizedDescription() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Description");
    }

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }
}
