package org.jphototagger.program.controller.nometadata;

import java.io.File;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.api.image.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.SortThumbnailsController;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Listens to selections within the list {@code AppPanel#getListNoMetadata()}
 * and when an item was selected, sets files without metadata related to the
 * selected item to the thumbnails panel.
 *
 * @author Elmar Baumann Elmar Baumann
 */
public final class NoMetadataItemSelectedController implements ListSelectionListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public NoMetadataItemSelectedController() {
        listen();
    }

    private void listen() {
        GUI.getNoMetadataList().addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setFiles();
        }
    }

    private void setFiles() {
        WaitDisplay.show();

        Object selValue = GUI.getNoMetadataList().getSelectedValue();

        if (selValue instanceof MetaDataValue) {
            List<File> imageFiles = repo.findImageFilesWithoutDataValue((MetaDataValue) selValue);

            setTitle((MetaDataValue) selValue);

            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            SortThumbnailsController.setLastSort();
            tnPanel.setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
            WaitDisplay.hide();
        }
    }

    private void setTitle(MetaDataValue mdValue) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(NoMetadataItemSelectedController.class,
                "NoMetadataItemSelectedController.AppFrame.Title.WithoutMetadata", mdValue.getDescription()));
    }
}
