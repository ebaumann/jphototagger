package org.jphototagger.repositoryfilebrowser;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.MainMenuManger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleImpl implements Module {

    @Override
    public void start() {
        MainMenuManger menuManager = Lookup.getDefault().lookup(MainMenuManger.class);

        menuManager.addToWindowMenu(BrowseRepositoryFilesAction.INSTANCE);
    }

    @Override
    public void close() {
        // Ignore
    }
}
