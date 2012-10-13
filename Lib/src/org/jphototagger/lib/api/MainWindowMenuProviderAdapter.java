package org.jphototagger.lib.api;

import java.util.Collection;
import java.util.Collections;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;

/**
 * @author Elmar Baumann
 */
public class MainWindowMenuProviderAdapter implements MainWindowMenuProvider {

    @Override
    public Collection<? extends MenuItemProvider> getFileMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getEditMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getViewMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getGotoMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getToolsMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getWindowMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getHelpMenuItems() {
        return Collections.emptyList();
    }

}
