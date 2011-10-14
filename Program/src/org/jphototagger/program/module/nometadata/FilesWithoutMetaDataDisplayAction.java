package org.jphototagger.program.module.nometadata;

import java.io.File;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann Elmar Baumann
 */
public final class FilesWithoutMetaDataDisplayAction implements ListSelectionListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public FilesWithoutMetaDataDisplayAction() {
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

        Object selValue = GUI.getNoMetadataList().getSelectedValue();

        if (selValue instanceof MetaDataValue) {
            WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
            waitDisplayer.show();
            setTitle((MetaDataValue) selValue);
            displayThumbnails((MetaDataValue) selValue);
            waitDisplayer.hide();
        }
    }

    private void displayThumbnails(MetaDataValue selValue) {
        List<File> imageFiles = repo.findImageFilesWithoutDataValue(selValue);
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        thumbnailsDisplayer.displayThumbnails(imageFiles, OriginOfDisplayedThumbnails.FILES_MATCHING_MISSING_METADATA);
    }

    private void setTitle(MetaDataValue metaDataValue) {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(
                Bundle.getString(FilesWithoutMetaDataDisplayAction.class,
                "FilesWithoutMetaDataDisplayAction.MainWindowTitle", metaDataValue.getDescription()));
    }
}
