package org.jphototagger.program.controller.thumbnail;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.tasks.AutomaticTask;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

/**
 * Listens to the {@code ThumbnailsPanel} and when the displayed
 * thumbnails were changed
 * this controller gives the new displayed files to an
 * {@code InsertImageFilesIntoDatabase} object which updates the database when
 * the displayed image files or XMP sidecar files are newer than their
 * metadata and thumbnails stored in the database.
 *
 * Runs as a {@code AutomaticTask}, that means if an other automatic task is
 * started, the update will be cancelled.
 *
 * @author Elmar Baumann
 */
public final class CreateMetadataOfDisplayedThumbnailsController {

    private static final Logger LOGGER = Logger.getLogger(CreateMetadataOfDisplayedThumbnailsController.class.getName());

    public CreateMetadataOfDisplayedThumbnailsController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        updateMetadata(evt.getImageFiles());
    }

    private synchronized void updateMetadata(List<File> imageFiles) {
        LOGGER.log(Level.INFO, "Synchronizing displayed thumbnails with the database");

        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(imageFiles, InsertIntoRepository.OUT_OF_DATE);
        String pBarString = Bundle.getString(CreateMetadataOfDisplayedThumbnailsController.class, "CreateMetadataOfDisplayedThumbnailsController.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        AutomaticTask.INSTANCE.setTask(inserter);
    }
}
