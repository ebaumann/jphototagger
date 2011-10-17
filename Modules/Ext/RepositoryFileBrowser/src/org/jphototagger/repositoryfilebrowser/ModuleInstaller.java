package org.jphototagger.repositoryfilebrowser;

import java.util.Arrays;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.util.Bundle;

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
        // Ignore
    }

    @Override
    public void remove() {
        // Ignore
    }

    @Override
    public Collection<? extends MenuItemProvider> getWindowMenuItems() {
        return Arrays.asList(BrowseRepositoryFilesAction.INSTANCE);
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
