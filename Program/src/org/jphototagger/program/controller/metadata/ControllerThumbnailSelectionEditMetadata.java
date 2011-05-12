package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.ThumbnailsPanel;


import java.io.File;

import java.util.List;

import javax.swing.JLabel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the {@link ThumbnailsPanel} for thumbnail selections.
 * If one or more thumbnails were selected, this controller enables or disables
 * edit metadata of the selcted thumbnails depending on write privileges in the
 * filesystem.
 *
 * This controller also sets the metadata of a selected thumbnail to the edit
 * panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailSelectionEditMetadata implements ThumbnailsPanelListener {
    public ControllerThumbnailSelectionEditMetadata() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }

    @Override
    public void thumbnailsSelectionChanged() {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean canEdit = false;
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

                if (tnPanel.isAFileSelected()) {
                    canEdit = canEdit();
                    setEnabled(canEdit);
                    GUI.getEditPanel().setImageFiles(tnPanel.getSelectedFiles());
                } else {
                    GUI.getEditPanel().emptyPanels(false);
                    setEnabled(false);
                }

                setInfoLabel(canEdit);
            }
        });
    }

    private void setEnabled(boolean enabled) {
        GUI.getAppPanel().getButtonEmptyMetadata().setEnabled(enabled);
        GUI.getEditPanel().setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        JLabel labelEditable = GUI.getAppPanel().getLabelMetadataInfoEditable();

        labelEditable.setText(canEdit
                              ? multipleThumbnailsSelected()
                                ? JptBundle.INSTANCE.getString(
                                    "ControllerThumbnailSelectionEditMetadata.Info.MetadataEditAddOnlyChanges")
                                : JptBundle.INSTANCE.getString(
                                    "ControllerThumbnailSelectionEditMetadata.Info.EditIsEnabled")
                              : JptBundle.INSTANCE.getString(
                                  "ControllerThumbnailSelectionEditMetadata.Info.EditIsDisabled"));
    }

    private boolean multipleThumbnailsSelected() {
        return GUI.getThumbnailsPanel().getSelectionCount() > 1;
    }

    private boolean canEdit() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        for (File selFile : selFiles) {
            if (!XmpMetadata.canWriteSidecarFileForImageFile(selFile)) {
                return false;
            }
        }

        return selFiles.size() > 0;
    }
}
