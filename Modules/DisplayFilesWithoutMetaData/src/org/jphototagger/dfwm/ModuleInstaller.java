package org.jphototagger.dfwm;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.lib.api.MainWindowComponentProviderAdapter;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = Module.class),
    @ServiceProvider(service = MainWindowComponentProvider.class)
})
public final class ModuleInstaller extends MainWindowComponentProviderAdapter implements Module, ModuleDescription {

    private static final Icon ICON = org.jphototagger.resources.Icons.getIcon("icon_no_metadata.png");
    private static final String TITLE = Bundle.getString(ModuleInstaller.class, "Module.Title");
    private final FilesWithoutMetaDataPanel panel = new FilesWithoutMetaDataPanel();

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        panel.writePreferences();
    }

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowSelectionComponents() {
        return Arrays.asList(mainWindowPanel);
    }
    private final MainWindowComponent mainWindowPanel = new MainWindowComponent() {

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

        @Override
        public KeyStroke getOptionalSelectionAccelaratorKey() {
            return null;
        }
    };

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }

    @Override
    public String getLocalizedDescription() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Description");
    }
}
