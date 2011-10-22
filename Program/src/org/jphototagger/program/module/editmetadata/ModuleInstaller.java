package org.jphototagger.program.module.editmetadata;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.Icon;

import javax.swing.JPanel;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
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
public final class ModuleInstaller extends MainWindowComponentProviderAdapter implements Module, ModuleDescription, SelectedFilesMetaDataEditor {

    private static final EditMetaDataPanelsWrapperPanel EDIT_METADTA_PANELS_WRAPPER = new EditMetaDataPanelsWrapperPanel(); // Has to be static!
    private static final EditMetaDataPanels EDIT_METADATA_PANELS = EDIT_METADTA_PANELS_WRAPPER.getEditMetadtaPanels(); // Has to be static!
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

    @Override
    public boolean isEditable() {
        return EDIT_METADATA_PANELS.isEditable();
    }

    @Override
    public void setOrAddText(MetaDataValue metaDataValue, String text) {
        EDIT_METADATA_PANELS.setOrAddText(metaDataValue, text);
    }

    @Override
    public void removeText(MetaDataValue metaDataValue, String text) {
        EDIT_METADATA_PANELS.removeText(metaDataValue, text);
    }

    @Override
    public void setRating(Long rating) {
        EDIT_METADATA_PANELS.setRating(rating);
    }

    @Override
    public void setXmp(Xmp xmp) {
        EDIT_METADATA_PANELS.setXmp(xmp);
    }

    @Override
    public void saveIfDirtyAndInputIsSaveEarly() {
        EDIT_METADATA_PANELS.saveIfDirtyAndInputIsSaveEarly();
    }

    @Override
    public void setFocusToLastFocussedEditControl() {
        EDIT_METADATA_PANELS.setFocusToLastFocussedEditControl();
    }

    @Override
    public void setMetadataTemplate(MetadataTemplate template) {
        EDIT_METADATA_PANELS.setMetadataTemplate(template);
    }

    @Override
    public JPanel getEditPanelForMetaDataValue(MetaDataValue metaDataValue) {
        return EDIT_METADATA_PANELS.getEditPanelForMetaDataValue(metaDataValue);
    }

    @Override
    public Xmp createXmpFromInput() {
        return EDIT_METADATA_PANELS.createXmpFromInput();
    }
}
