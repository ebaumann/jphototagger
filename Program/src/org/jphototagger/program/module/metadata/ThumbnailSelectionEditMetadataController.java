package org.jphototagger.program.module.metadata;

import java.io.File;
import java.util.List;

import javax.swing.JLabel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Listens to the {@code ThumbnailsPanel} for thumbnail selections.
 * If one or more thumbnails were selected, this controller enables or disables
 * edit metadata of the selcted thumbnails depending on write privileges in the
 * filesystem.
 *
 * This controller also sets the metadata of a selected thumbnail to the edit
 * panel.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailSelectionEditMetadataController {

    public ThumbnailSelectionEditMetadataController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                boolean canEdit = false;
                ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
                EditMetadataPanels editPanel = GUI.getEditPanel();

                if (tnPanel.isAFileSelected()) {
                    canEdit = canEdit();
                    setEnabled(canEdit);
                    editPanel.setFiles(tnPanel.getSelectedFiles());
                } else {
                    editPanel.clear();
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
                ? Bundle.getString(ThumbnailSelectionEditMetadataController.class,
                "ThumbnailSelectionEditMetadataController.Info.MetadataEditAddOnlyChanges")
                : Bundle.getString(ThumbnailSelectionEditMetadataController.class,
                "ThumbnailSelectionEditMetadataController.Info.EditIsEnabled")
                : Bundle.getString(ThumbnailSelectionEditMetadataController.class,
                "ThumbnailSelectionEditMetadataController.Info.EditIsDisabled"));
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
