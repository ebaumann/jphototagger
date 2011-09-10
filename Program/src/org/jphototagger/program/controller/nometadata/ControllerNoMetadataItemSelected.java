package org.jphototagger.program.controller.nometadata;

import java.io.File;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 * Listens to selections within the list {@link AppPanel#getListNoMetadata()}
 * and when an item was selected, sets files without metadata related to the
 * selected item to the thumbnails panel.
 *
 * @author Elmar Baumann Elmar Baumann
 */
public final class ControllerNoMetadataItemSelected implements ListSelectionListener {

    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    public ControllerNoMetadataItemSelected() {
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
            List<File> imageFiles = repo.getImageFilesWithoutDataValue((MetaDataValue) selValue);

            setTitle((MetaDataValue) selValue);

            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            ControllerSortThumbnails.setLastSort();
            tnPanel.setFiles(imageFiles, TypeOfDisplayedImages.MISSING_METADATA);
            WaitDisplay.hide();
        }
    }

    private void setTitle(MetaDataValue mdValue) {
        GUI.getAppFrame().setTitle(
                Bundle.getString(ControllerNoMetadataItemSelected.class,
                "ControllerNoMetadataItemSelected.AppFrame.Title.WithoutMetadata", mdValue.getDescription()));
    }
}
