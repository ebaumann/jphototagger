package org.jphototagger.repositoryfilebrowser;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.MainWindowMenuManager;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module {

    @Override
    public void init() {
        MainWindowMenuManager menuManager = Lookup.getDefault().lookup(MainWindowMenuManager.class);
        menuManager.addToWindowMenu(BrowseRepositoryFilesAction.INSTANCE);
    }

    @Override
    public void remove() {
        // Ignore
    }

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }
}
