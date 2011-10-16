package org.jphototagger.api.windows;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface MainWindowMenuProvider {

    Collection<? extends MainWindowMenuItem> getFileMenuItems();

    Collection<? extends MainWindowMenuItem> getEditMenuItems();

    Collection<? extends MainWindowMenuItem> getViewMenuItems();

    Collection<? extends MainWindowMenuItem> getGotoMenuItems();

    Collection<? extends MainWindowMenuItem> getToolsMenuItems();

    Collection<? extends MainWindowMenuItem> getWindowMenuItems();

    Collection<? extends MainWindowMenuItem> getHelpMenuItems();
}
