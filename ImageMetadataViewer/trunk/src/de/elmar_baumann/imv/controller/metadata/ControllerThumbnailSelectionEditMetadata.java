package de.elmar_baumann.imv.controller.metadata;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Kontrolliert das Speichern von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerThumbnailSelectionEditMetadata implements ThumbnailsPanelListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JButton buttonSave = appPanel.getButtonSaveMetadata();
    private final JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private final JLabel labelMetadataInfoEditable = appPanel.getLabelMetadataInfoEditable();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerThumbnailSelectionEditMetadata() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsChanged() {
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            boolean canEdit = canEdit();
            setEnabled(canEdit);
            setEditPanelsContent();
            setInfoLabel(canEdit);
        } else {
            setEnabled(false);
        }
    }

    private void setEnabled(boolean enabled) {
        buttonSave.setEnabled(enabled);
        buttonEmpty.setEnabled(enabled);
        editPanels.setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        labelMetadataInfoEditable.setText(
                canEdit
                ? multipleThumbnailsSelected()
                ? Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.MetadataEditAddOnlyChanges")
                : Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.EditIsEnabled")
                : Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.EditIsDisabled"));
    }

    private boolean multipleThumbnailsSelected() {
        return thumbnailsPanel.getSelectionCount() > 1;
    }

    private boolean canEdit() {
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        for (String filename : filenames) {
            if (!XmpMetadata.canWriteSidecarFile(filename)) {
                return false;
            }
        }
        return filenames.size() > 0;
    }

    private void setEditPanelsContent() {
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        if (filenames.size() == 1) {
            List<XMPPropertyInfo> xmpPropertyInfos = XmpMetadata.getPropertyInfosOfFile(filenames.get(0));

            if (xmpPropertyInfos != null && xmpPropertyInfos.size() > 0) {
                editPanels.setXmpPropertyInfos(filenames, xmpPropertyInfos);
            } else {
                editPanels.emptyPanels(false);
                editPanels.setFilenames(filenames);
            }
        } else if (filenames.size() > 1) {
            editPanels.emptyPanels(false);
            editPanels.setFilenames(filenames);
        } else if (filenames.size() <= 0) {
            editPanels.emptyPanels(false);
        }
    }
}
