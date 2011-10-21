package org.jphototagger.userdefinedfilters;

import java.util.Arrays;
import java.util.Collection;
import org.jphototagger.api.windows.MenuItemProvider;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuProvider.class)
public final class MenuLayer extends MainWindowMenuProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getWindowMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new ShowUserDefinedFileFiltersDialogAction(), 30, true));
    }
}
