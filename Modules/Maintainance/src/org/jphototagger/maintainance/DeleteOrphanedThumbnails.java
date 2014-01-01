package org.jphototagger.maintainance;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class DeleteOrphanedThumbnails implements Runnable, Cancelable {

    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private int tnCount = 0;
    private int countDeleted = 0;
    private int currentFileIndex = 0;
    private volatile boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(DeleteOrphanedThumbnails.class.getName());
    private final ImageFilesRepository imageFilesRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public synchronized void addProgressListener(ProgressListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }
        ls.add(l);
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current process.
     */
    @Override
    public synchronized void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        ThumbnailsRepository  tnRepo = Lookup.getDefault().lookup(ThumbnailsRepository.class);
        Set<String> imageFilenames = tnRepo.getImageFilenames();
        tnCount = imageFilenames.size();
        notifyStarted();
        for (String imageFilename : imageFilenames) {
            if (cancel) {
                break;
            }
            currentFileIndex++;
            File imageFile = new File(imageFilename);
            boolean isDelete = !imageFilesRepo.existsImageFile(imageFile);
            if (isDelete) {
                logDelete(imageFile);
                tnRepo.deleteThumbnail(imageFile);
                countDeleted++;
                if (thumbnailsDisplayer.isDisplayFile(imageFile)) {
                    thumbnailsDisplayer.removeFilesFromDisplay(Arrays.asList(imageFile));
                }
            }
            notifyPerformed(imageFile, isDelete);
        }
        notifyEnded();
    }

    private synchronized void logDelete(File imageFile ) {
        LOGGER.log(Level.INFO, "Deleting orphaned thumbnail for image file ''{0}''", imageFile);
    }

    private synchronized void notifyStarted() {
        LOGGER.log(Level.INFO, "Verifying which of the {0} thumbnails are orphaned", tnCount);
        ProgressEvent evt = new ProgressEvent.Builder().source(this).
                minimum(0).
                maximum(tnCount).
                value(0).
                info(getStartMessage()).
                build();

        ls.notifyStarted(evt);
    }

    private void notifyPerformed(File file, boolean wasDeleted) {
        LOGGER.log(Level.FINEST, "Verifying wheter thumbnail ''{0}'' is orphaned", file);
        ProgressEvent evt = new ProgressEvent.Builder().source(this).
                minimum(0).
                maximum(tnCount).
                value(currentFileIndex).
                info(getPerformedMessage(file, wasDeleted)).
                build();
        ls.notifyPerformed(evt);
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent.Builder().source(this).
                minimum(0).
                maximum(tnCount).
                value(currentFileIndex).
                info(getEndMessage()).
                build();
        ls.notifyEnded(evt);
        LOGGER.log(Level.INFO, "Verifying of orphaned thumbnails finished. Deleted {0} thumbnails.", currentFileIndex);
    }

    private String getStartMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Start", tnCount);
    }

    private String getPerformedMessage(File file, boolean wasDeleted) {
        return wasDeleted
                ? Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Performed", file)
                : null;
    }

    private String getEndMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.End", countDeleted);
    }
}
