package org.jphototagger.program.repository.actions;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.storage.UserFilesProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 * Deletes thumbnails without an image file in the database.
 * <p>
 * Uses {@link DatabaseImageFiles#findAllThumbnailFiles()} to determine valid
 * thumbnail files. Then deletes all other files in the directory
 * {@link UserSettings#getThumbnailsDirectoryName()}.
 *
 * @author Elmar Baumann
 */
public final class DeleteOrphanedThumbnails implements Runnable, Cancelable {

    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private int countFilesInDir = 0;
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
        Set<File> imageFilesExisting = repo.findAllThumbnailFiles();
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File[] filesInDir = provider.getThumbnailsDirectory().listFiles();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        boolean isDelete = false;
        File fileInDir = null;

        countFilesInDir = filesInDir.length;
        notifyStarted();

        for (int i = 0; !cancel && (i < countFilesInDir); i++) {
            currentFileIndex = i + 1;
            fileInDir = filesInDir[i];
            isDelete = !imageFilesExisting.contains(fileInDir);

            if (isDelete && fileInDir.isFile()) {
                logDelete(fileInDir);

                if (fileInDir.delete()) {
                    countDeleted++;

                    if (tnPanel.containsFile(fileInDir)) {
                        tnPanel.removeFiles(Arrays.asList(fileInDir));
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Can't delete orphaned thumbnail ''{0}''!", fileInDir);
                }
            }

            notifyPerformed(fileInDir);
        }

        notifyEnded();
    }

    private synchronized void logDelete(File file) {
        LOGGER.log(Level.INFO, "Deleting orphaned thumbnail file ''{0}''", file);
    }

    private synchronized void notifyStarted() {
        LOGGER.log(Level.INFO, "Verifying which of the {0} thumbnails are orphaned", countFilesInDir);

        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir, 0, getStartMessage());

        ls.notifyStarted(evt);
    }

    private void notifyPerformed(File file) {
        LOGGER.log(Level.FINEST, "Verifying wheter thumbnail ''{0}'' is orphaned", file);

        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir, currentFileIndex, getPerformedMessage(file));

        ls.notifyPerformed(evt);
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir, currentFileIndex, getEndMessage());

        ls.notifyEnded(evt);
        LOGGER.log(Level.INFO, "Verifying of orphaned thumbnails finished. Deleted {0} thumbnails.", currentFileIndex);
    }

    private String getStartMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Start", countFilesInDir);
    }

    private String getPerformedMessage(File file) {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.Performed", file);
    }

    private String getEndMessage() {
        return Bundle.getString(DeleteOrphanedThumbnails.class, "DeleteOrphanedThumbnails.Info.End", countDeleted);
    }
}
