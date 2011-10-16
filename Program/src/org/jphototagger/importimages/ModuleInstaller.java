package org.jphototagger.importimages;

import java.util.Arrays;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MainWindowMenuItem;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.util.Bundle;
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
    public Collection<? extends MainWindowMenuItem> getFileMenuItems() {
        return Arrays.asList(new ImportImageFilesAction());
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
