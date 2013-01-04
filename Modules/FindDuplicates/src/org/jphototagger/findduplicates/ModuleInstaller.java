package org.jphototagger.findduplicates;

import java.util.Arrays;
import java.util.Collection;
import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
        @ServiceProvider(service = Module.class),
        @ServiceProvider(service = MainWindowMenuProvider.class)
})
public final class ModuleInstaller extends MainWindowMenuProviderAdapter implements Module, ModuleDescription {

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        // ignore
    }

    @Override
    public Collection<? extends MenuItemProvider> getWindowMenuItems() {
        return Arrays.asList(FindDuplicatesAction.INSTANCE);
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
