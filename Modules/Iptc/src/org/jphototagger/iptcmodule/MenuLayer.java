package org.jphototagger.iptcmodule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.domain.thumbnails.ThumbnailsPopupMenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service=MainWindowMenuProvider.class),
    @ServiceProvider(service=ThumbnailsPopupMenuItemProvider.class)
})
public final class MenuLayer extends MainWindowMenuProviderAdapter implements ThumbnailsPopupMenuItemProvider {

    @Override
    public Collection<? extends MenuItemProvider> getMetaDataMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new ExportIptcToXmpOfSelectedFilesAction(), 200, true));
    }

    @Override
    public Collection<? extends MenuItemProvider> getToolsMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new ShowIptcToXmpDialogAction(), 100, false));
    }

    @Override
    public Collection<? extends MenuItemProvider> getRootMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getRefreshMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getFileOperationsMenuItems() {
        return Collections.emptyList();
    }
}
