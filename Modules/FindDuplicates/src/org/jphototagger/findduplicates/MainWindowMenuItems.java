package org.jphototagger.findduplicates;

import java.util.Arrays;
import java.util.Collection;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuProvider.class)
public final class MainWindowMenuItems extends MainWindowMenuProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getToolsMenuItems() {
        return Arrays.asList(new MenuItemProvider() {

            @Override
            public JMenuItem getMenuItem() {
                return new JMenuItem(new FindDuplicatesAction());
            }

            @Override
            public int getPosition() {
                return 20000;
            }

            @Override
            public boolean isSeparatorBefore() {
                return true;
            }
        });
    }
}
