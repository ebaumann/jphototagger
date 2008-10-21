package de.elmar_baumann.imv.controller.metadata;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.MetadataEditPanelsArray;
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
public class ControllerThumbnailSelectionEditMetadata
    extends Controller implements ThumbnailsPanelListener, DatabaseListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonSave = appPanel.getButtonSaveMetadata();
    private JLabel labelMetadataInfoEditable = appPanel.getLabelMetadataInfoEditable();
    private MetadataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerThumbnailSelectionEditMetadata() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        DatabaseImageFiles.getInstance().addDatabaseListener(this);
    }

    @Override
    public void thumbnailsChanged() {
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        if (isControl()) {
            if (thumbnailsPanel.getSelectionCount() > 0) {
                boolean canEdit = canEdit();
                setEnabled(canEdit);
                setEditPanelsContent();
                setInfoLabel(canEdit);
            } else {
                setEnabled(false);
            }
        }
    }

    private void setEnabled(boolean enabled) {
        buttonSave.setEnabled(enabled);
        editPanels.setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        labelMetadataInfoEditable.setText(
            canEdit
            ? multipleThumbnailsSelected()
            ? Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.MetaDataEditAddOnlyChanges")
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
        editPanels.emptyPanels();
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        if (filenames.size() == 1) {
            XmpMetadata xmpMetaData = new XmpMetadata();
            List<XMPPropertyInfo> xmpPropertyInfos = xmpMetaData.getPropertyInfosOfFile(filenames.get(0));

            if (xmpPropertyInfos != null && xmpPropertyInfos.size() > 0) {
                editPanels.setXmpPropertyInfos(filenames, xmpPropertyInfos);
            }
        } else if (filenames.size() > 1) {
            editPanels.setFilenames(filenames);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type actionType = action.getType();
        if (isControl() && (actionType.equals(DatabaseAction.Type.ImageFileInserted) ||
            actionType.equals(DatabaseAction.Type.ImageFileUpdated))) {
            showUpdates(action.getImageFileData().getFilename());
        } else if (isControl() && actionType.equals(DatabaseAction.Type.XmpUpdated)) {
            showUpdates(action.getFilename());
        }
    }

    private void showUpdates(String filename) {
        if (thumbnailsPanel.getSelectionCount() == 1) {
            String selectedFilename = thumbnailsPanel.getSelectedFiles().get(0).getAbsolutePath();
            if (filename.equals(selectedFilename)) {
                setEditPanelsContent();
            }
        }
    }
}
