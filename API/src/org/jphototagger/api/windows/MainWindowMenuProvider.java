package org.jphototagger.api.windows;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface MainWindowMenuProvider {

    Collection<? extends MenuItemProvider> getFileMenuItems();

    Collection<? extends MenuItemProvider> getEditMenuItems();

    Collection<? extends MenuItemProvider> getViewMenuItems();

    Collection<? extends MenuItemProvider> getGotoMenuItems();

    Collection<? extends MenuItemProvider> getToolsMenuItems();

    Collection<? extends MenuItemProvider> getWindowMenuItems();

    Collection<? extends MenuItemProvider> getHelpMenuItems();
}
