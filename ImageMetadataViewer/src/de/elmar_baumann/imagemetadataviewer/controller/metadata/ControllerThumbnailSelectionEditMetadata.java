package de.elmar_baumann.imagemetadataviewer.controller.metadata;

import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseAction;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseListener;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelAction;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.MetaDataEditPanelsArray;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Kontrolliert das Speichern von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/21
 */
public class ControllerThumbnailSelectionEditMetadata
    extends Controller implements ThumbnailsPanelListener, DatabaseListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonSave = appPanel.getButtonSaveMetadata();
    private JLabel labelMetadataInfoEditable = appPanel.getLabelMetadataInfoEditable();
    private MetaDataEditPanelsArray editPanels = appPanel.getEditPanelsArray();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();

    public ControllerThumbnailSelectionEditMetadata() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        Database.getInstance().addDatabaseListener(this);
    }

    @Override
    public void thumbnailSelected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            boolean canEdit = canEdit();
            buttonSave.setEnabled(canEdit);
            editPanels.setEditable(canEdit);
            setEditPanelsContent();
            labelMetadataInfoEditable.setText(canEdit
                ? multipleThumbnailsSelected()
                ? Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.MetaDataEditAddOnlyChanges")
                : Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.EditIsEnabled")
                : Bundle.getString("ControllerThumbnailSelectionEditMetadata.InformationMessage.EditIsDisabled"));
        }
    }

    @Override
    public void thumbnailCountChanged() {
        // Nichts tun
    }

    private boolean multipleThumbnailsSelected() {
        return thumbnailsPanel.getSelectionCount() > 1;
    }

    private boolean canEdit() {
        Vector<String> filenames = thumbnailsPanel.getSelectedFilenames();
        for (String filename : filenames) {
            if (!XmpMetadata.canWriteSidecarFile(filename)) {
                return false;
            }
        }
        return filenames.size() > 0;
    }

    @Override
    public void allThumbnailsDeselected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            buttonSave.setEnabled(false);
            editPanels.setEditable(false);
        }
    }

    private void setEditPanelsContent() {
        editPanels.emptyPanels();
        Vector<String> filenames = thumbnailsPanel.getSelectedFilenames();
        if (filenames.size() == 1) {
            XmpMetadata xmpMetaData = new XmpMetadata();
            Vector<XMPPropertyInfo> xmpPropertyInfos = xmpMetaData.getPropertyInfosOfFile(filenames.get(0));

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
        if (isStarted() && (actionType.equals(DatabaseAction.Type.ImageFileInserted) ||
            actionType.equals(DatabaseAction.Type.ImageFileUpdated))) {
            showUpdates(action.getImageFileData().getFilename());
        } else if (isStarted() && actionType.equals(DatabaseAction.Type.XmpUpdated)) {
            showUpdates(action.getFilename());
        }
    }

    private void showUpdates(String filename) {
        if (thumbnailsPanel.getSelectionCount() == 1) {
            String selectedFilename = thumbnailsPanel.getSelectedFilenames().get(0);
            if (filename.equals(selectedFilename)) {
                setEditPanelsContent();
            }
        }
    }
}
