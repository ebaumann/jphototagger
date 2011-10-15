package org.jphototagger.program.module.dfwm;

import java.awt.Component;

import javax.swing.Icon;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Module.class)
public final class ModuleInstaller implements Module {

    private static final Icon ICON = IconUtil.getImageIcon(ModuleInstaller.class, "files_without_metadata.png");
    private static final String TITLE = Bundle.getString(ModuleInstaller.class, "Module.Title");
    private final FilesWithoutMetaDataPanel panel = new FilesWithoutMetaDataPanel();


    @Override
    public void init() {
        plugIntoMainWindow();

    }

    @Override
    public void remove() {
        panel.writePreferences();
    }

    private void plugIntoMainWindow() {
        MainWindowManager manager = Lookup.getDefault().lookup(MainWindowManager.class);
        manager.dockIntoSelectionWindow(mainWindowComponent);
    }
    private final MainWindowComponent mainWindowComponent = new MainWindowComponent() {

        @Override
        public Component getComponent() {
            return panel;
        }

        @Override
        public Icon getSmallIcon() {
            return ICON;
        }

        @Override
        public Icon getLargeIcon() {
            return null;
        }

        @Override
        public int getPosition() {
            return 8;
        }

        @Override
        public String getTitle() {
            return TITLE;
        }

        @Override
        public String getTooltipText() {
            return null;
        }
    };

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }
}
