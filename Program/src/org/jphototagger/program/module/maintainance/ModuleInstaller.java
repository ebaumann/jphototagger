package org.jphototagger.program.module.maintainance;

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
        menuManager.addToFileMenu(new ShowUpdateMetadataOfDirectoriesDialogAction());
        menuManager.addToFileMenu(new ShowMaintainanceDialogAction());
        menuManager.addToFileMenu(new ShowRepositoryMaintainanceDialogAction());
    }

    @Override
    public void remove() {
        // ignored
    }

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }
}
