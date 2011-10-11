package org.jphototagger.program.module.maintainance;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class DeleteOrphanedThumbnails implements Runnable, Cancelable {

    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private int fileCount = 0;
    private int countDeleted = 0;
    private int currentFileIndex = 0;
    private volatile boolean cancel;
    private static final Logger LOGGER = Logger.getLogger(DeleteOrphanedThumbnails.class.getName());
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public synchronized void addProgressListener(ProgressListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        ls.add(l);
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    @Override
    public synchronized void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        Set<File> allThumbnailFilesKnownByRepository = repo.findAllThumbnailFiles();
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File thumbnailsDirectory = provider.getThumbnailsDirectory();
        File[] filesInThumbnailDirectory = thumbnailsDirectory.listFiles();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        boolean isDelete = false;
        File fileInThumbnailDirectory = null;

        fileCount = filesInThumbnailDirectory.length;
        notifyStarted();

        for (int i = 0; !cancel && (i < fileCount); i++) {
            currentFileIndex = i + 1;
            fileInThumbnailDirectory = filesInThumbnailDirectory[i];
            isDelete = !allThumbnailFilesKnownByRepository.contains(fileInThumbnailDirectory);

            if (isDelete && fileInThumbnailDirectory.isFile()) {
                logDelete(fileInThumbnailDirectory);

                if (fileInThumbnailDirectory.delete()) {
                    countDeleted++;

                    if (tnPanel.containsFile(fileInThumbnailDirectory)) {
                        tnPanel.removeFiles(Arrays.asList(fileInThumbnailDirectory));
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Can''t delete orphaned thumbnail ''{0}''!", fileInThumbnailDirectory);
                }
            }

            notifyPerformed(fileInThumbnailDirectory);
        }

        notifyEnded();
    }

    private synchronized void logDelete(File file) {
        LOGGER.log(Level.INFO, "Deleting orphaned thumbnail file ''{0}''", file);
    }

    private synchronized void notifyStarted() {
        LOGGER.log(Level.INFO, "Verifying which of the {0} thumbnails are orphaned", fileCount);

        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(fileCount)
                .value(0)
                .info(getStartMessage())
                .build();

        ls.notifyStarted(evt);
    }

    private void notifyPerformed(File file) {
        LOGGER.log(Level.FINEST, "Verifying wheter thumbnail ''{0}'' is orphaned", file);

        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(fileCount)
                .value(currentFileIndex)
                .info(getPerformedMessage(file))
                .build();

        ls.notifyPerformed(evt);
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent.Builder()
                .source(this)
                .minimum(0)
                .maximum(fileCount)
                .value(currentFileIndex)
                .info(getEndMessage())
                .build();

        ls.notifyEnded(evt);
        LOGGER.log(Level.INFO, "Verifying of orphaned thumbnails finished. Deleted {0} thumbnails.", currentFileIndex);
    }

    private String getStartMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Start", fileCount);
    }

    private String getPerformedMessage(File file) {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Performed", file);
    }

    private String getEndMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.End", countDeleted);
    }
}
