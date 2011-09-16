package org.jphototagger.repositoryfilebrowser;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.MainWindowMenuManager;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleImpl implements Module {

    @Override
    public void start() {
        MainWindowMenuManager menuManager = Lookup.getDefault().lookup(MainWindowMenuManager.class);

        menuManager.addToWindowMenu(BrowseRepositoryFilesAction.INSTANCE);
    }

    @Override
    public void close() {
        // Ignore
    }
}
