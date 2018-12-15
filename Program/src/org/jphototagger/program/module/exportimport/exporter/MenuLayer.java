package org.jphototagger.program.module.exportimport.exporter;

import java.util.Arrays;
import java.util.Collection;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuProvider.class)
public final class MenuLayer extends MainWindowMenuProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getFileMenuItems() {
        return Arrays.asList(new MenuItemProvider() {

            @Override
            public JMenuItem getMenuItem() {
                JMenu menuExport = UiFactory.menu(Bundle.getString(MenuLayer.class, "MenuLayer.Name"));
                menuExport.setIcon(Icons.getIcon("icon_export.png"));
                MnemonicUtil.setMnemonics(menuExport);
                JMenuItem itemExport = UiFactory.menuItem(new JptExportAction());
                MnemonicUtil.setMnemonics(itemExport);
                menuExport.add(itemExport);
                return menuExport;
            }

            @Override
            public boolean isSeparatorBefore() {
                return true;
            }

            @Override
            public int getPosition() {
                return 500;
            }
        });
    }
}
