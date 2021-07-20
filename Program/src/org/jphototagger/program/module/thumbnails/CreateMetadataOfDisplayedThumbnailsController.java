package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.concurrent.ReplaceableTask;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.openide.util.Lookup;

/**
 * Listens to the {@code ThumbnailsPanel} and when the displayed
 * thumbnails were changed
 * this controller gives the new displayed files to an
 * {@code SaveToOrUpdateFilesInRepositoryImpl} object which updates the repository when
 * the displayed image files or XMP sidecar files are newer than their
 * metadata and thumbnails stored in the repository.
 *
 * @author Elmar Baumann
 */
public final class CreateMetadataOfDisplayedThumbnailsController {

    private static final Logger LOGGER = Logger.getLogger(CreateMetadataOfDisplayedThumbnailsController.class.getName());
    private final ReplaceableTask replaceableTask = Lookup.getDefault().lookup(ReplaceableTask.class);

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
        LOGGER.log(Level.INFO, "Synchronizing displayed thumbnails with the repository");

        SaveToOrUpdateFilesInRepositoryImpl inserter = new SaveToOrUpdateFilesInRepositoryImpl(imageFiles, SaveOrUpdate.OUT_OF_DATE);
        String pBarString = Bundle.getString(CreateMetadataOfDisplayedThumbnailsController.class, "CreateMetadataOfDisplayedThumbnailsController.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));
        replaceableTask.replacePreviousTaskWith(inserter);
    }
}
