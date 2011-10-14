package org.jphototagger.program.module.nometadata;

import java.io.File;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jphototagger.api.windows.MainWindowManager;
import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.WaitDisplay;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;

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
        WaitDisplay.INSTANCE.show();

        Object selValue = GUI.getNoMetadataList().getSelectedValue();

        if (selValue instanceof MetaDataValue) {
            List<File> imageFiles = repo.findImageFilesWithoutDataValue((MetaDataValue) selValue);

            setTitle((MetaDataValue) selValue);

            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            tnPanel.setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
            WaitDisplay.INSTANCE.hide();
        }
    }

    private void setTitle(MetaDataValue mdValue) {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(
                Bundle.getString(NoMetadataItemSelectedController.class,
                "NoMetadataItemSelectedController.AppFrame.Title.WithoutMetadata", mdValue.getDescription()));
    }
}
