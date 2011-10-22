package org.jphototagger.program.module.editmetadata;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.Icon;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.lib.api.MainWindowComponentProviderAdapter;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = Module.class),
    @ServiceProvider(service = MainWindowComponentProvider.class)
})
public final class ModuleInstaller extends MainWindowComponentProviderAdapter implements Module, ModuleDescription, EditMetaDataPanelsProvider {

    private static final EditMetaDataPanelsWrapperPanel EDIT_METADTA_PANELS_WRAPPER = new EditMetaDataPanelsWrapperPanel(); // Has to be static!
    private static final Icon ICON = IconUtil.getImageIcon(ModuleInstaller.class, "edit.png");
    private static final String TITLE = Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Title");

    @Override
    public void init() {
        // ignore
    }

    @Override
    public void remove() {
        // ignore
    }

    @Override
    public String getLocalizedDescription() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Description");
    }

    @Override
    public String toString() {
        return Bundle.getString(ModuleInstaller.class, "ModuleInstaller.Name");
    }

    @Override
    public EditMetaDataPanels getEditMetadataPanels() {
        return EDIT_METADTA_PANELS_WRAPPER.getEditMetadtaPanels();
    }

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowEditComponents() {
        return Arrays.asList(mainWindowPanel);
    }

    private final MainWindowComponent mainWindowPanel = new MainWindowComponent() {

        @Override
        public Component getComponent() {
            return EDIT_METADTA_PANELS_WRAPPER;
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
            return 3;
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
}
