package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
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
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link ImageFileThumbnailsPanel} for thumbnail selections.
 * If one or more thumbnails were selected, this controller enables or disables
 * edit metadata of the selcted thumbnails depending on write privileges in the
 * filesystem.
 *
 * This controller also sets the metadata of a selected thumbnail to the edit
 * panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerThumbnailSelectionEditMetadata implements
        ThumbnailsPanelListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JButton buttonEmpty = appPanel.getButtonEmptyMetadata();
    private final JLabel labelMetadataInfoEditable =
            appPanel.getLabelMetadataInfoEditable();
    private final EditMetadataPanelsArray editPanels =
            appPanel.getEditPanelsArray();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

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
    public void thumbnailsSelectionChanged() {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (thumbnailsPanel.getSelectionCount() > 0) {
                    boolean canEdit = canEdit();
                    setEnabled(canEdit);
                    editPanels.setFilenames(FileUtil.getAsFilenames(thumbnailsPanel.
                            getSelectedFiles()));
                    setInfoLabel(canEdit);
                } else {
                    appPanel.getEditPanelsArray().emptyPanels(false);
                    setEnabled(false);
                }
            }
        });
    }

    private void setEnabled(boolean enabled) {
        buttonEmpty.setEnabled(enabled);
        editPanels.setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        labelMetadataInfoEditable.setText(
                canEdit
                ? multipleThumbnailsSelected()
                  ? Bundle.getString(
                "ControllerThumbnailSelectionEditMetadata.Info.MetadataEditAddOnlyChanges") // NOI18N
                  : Bundle.getString(
                "ControllerThumbnailSelectionEditMetadata.Info.EditIsEnabled") // NOI18N
                : Bundle.getString(
                "ControllerThumbnailSelectionEditMetadata.Info.EditIsDisabled")); // NOI18N
    }

    private boolean multipleThumbnailsSelected() {
        return thumbnailsPanel.getSelectionCount() > 1;
    }

    private boolean canEdit() {
        List<String> filenames =
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        for (String filename : filenames) {
            if (!XmpMetadata.canWriteSidecarFileForImageFile(filename)) {
                return false;
            }
        }
        return filenames.size() > 0;
    }
                }
