package org.jphototagger.api.windows;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Elmar Baumann
 */
public class MainWindowMenuProviderAdapter implements MainWindowMenuProvider {

    @Override
    public Collection<? extends MainWindowMenuItem> getFileMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getEditMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getViewMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getGotoMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getToolsMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getWindowMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowMenuItem> getHelpMenuItems() {
        return Collections.emptyList();
    }

}
