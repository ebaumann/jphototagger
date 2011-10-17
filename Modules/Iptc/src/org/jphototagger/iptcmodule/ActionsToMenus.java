package org.jphototagger.iptcmodule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MainWindowMenuProviderAdapter;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.domain.thumbnails.ThumbnailsPopupMenuItemProvider;
import org.jphototagger.lib.swing.util.MenuUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service=MainWindowMenuProvider.class),
    @ServiceProvider(service=ThumbnailsPopupMenuItemProvider.class)
})
public final class ActionsToMenus extends MainWindowMenuProviderAdapter implements ThumbnailsPopupMenuItemProvider {

    @Override
    public Collection<? extends MenuItemProvider> getMetaDataMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new ExportIptcToXmpOfSelectedFilesAction(), 2, true));
    }

    @Override
    public Collection<? extends MenuItemProvider> getToolsMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new ShowIptcToXmpDialogAction(), 0, false));
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

    public static class MenuItemProviderImpl implements MenuItemProvider {
        private final Action action;
        private final int position;
        private final boolean isSeparatorBefore;

        public MenuItemProviderImpl(Action action, int position, boolean isSeparatorBefore) {
            this.action = action;
            this.position = position;
            this.isSeparatorBefore = isSeparatorBefore;
        }

        @Override
        public JMenuItem getMenuItem() {
            JMenuItem item = new JMenuItem(action);
            MenuUtil.setMnemonics(item);
            return item;
        }

        @Override
        public boolean isSeparatorBefore() {
            return isSeparatorBefore;
        }

        @Override
        public int getPosition() {
            return position;
        }
    }
}
