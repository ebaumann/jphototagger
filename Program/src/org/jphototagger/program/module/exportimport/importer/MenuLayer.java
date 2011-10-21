package org.jphototagger.program.module.exportimport.importer;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;

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
                JMenu menuImport = new JMenu(Bundle.getString(MenuLayer.class, "MenuLayer.Name"));
                menuImport.setIcon(IconUtil.getImageIcon(MenuLayer.class, "import.png"));
                MnemonicUtil.setMnemonics(menuImport);
                menuImport.add(new JMenuItem(new JptImportAction()));
                return menuImport;
            }

            @Override
            public boolean isSeparatorBefore() {
                return false;
            }

            @Override
            public int getPosition() {
                return 600;
            }
        });
    }
}
