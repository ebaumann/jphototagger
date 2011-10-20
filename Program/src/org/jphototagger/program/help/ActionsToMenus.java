package org.jphototagger.program.help;

import java.util.Arrays;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuProvider.class)
public final class ActionsToMenus extends MainWindowMenuProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getHelpMenuItems() {
        return Arrays.<MenuItemProvider>asList(
                new MenuItemProviderImpl(new ShowHelpAction(), 100, false),
                new MenuItemProviderImpl(new ShowPdfUserManualAction(), 200, false),
                new MenuItemProviderImpl(new ShowAcceleratorKeysHelpAction(), 300, false),
                new MenuItemProviderImpl(new BrowseUserForumAction(), 400, true),
                new MenuItemProviderImpl(new BrowseWebsiteAction(), 500, false),
                new MenuItemProviderImpl(new BrowseChangeLogAction(), 600, false),
                new MenuItemProviderImpl(new SendBugReportMailAction(), 700, true),
                new MenuItemProviderImpl(new SendIssueMailAction(), 800, false)
                );
    }
}
