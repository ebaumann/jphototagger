package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.Arrays;
import java.util.Set;
import org.jphototagger.program.resource.GUI;

/**
 * Deletes thumbnails without an image file in the database.
 * <p>
 * Uses {@link DatabaseImageFiles#getAllThumbnailFiles()} to determine valid
 * thumbnail files. Then deletes all other files in the directory
 * {@link UserSettings#getThumbnailsDirectoryName()}.
 *
 * @author Elmar Baumann
 */
public final class DeleteOrphanedThumbnails implements Runnable, Cancelable {
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private int                           countFilesInDir  = 0;
    private int                           countDeleted     = 0;
    private int                           currentFileIndex = 0;
    private volatile boolean              cancel;

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
        Set<File> imageFilesExisting =
            DatabaseImageFiles.INSTANCE.getAllThumbnailFiles();
        File[] filesInDir =
            new File(
                UserSettings.INSTANCE.getThumbnailsDirectoryName()).listFiles();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        boolean isDelete  = false;
        File    fileInDir = null;

        countFilesInDir = filesInDir.length;
        notifyStarted();

        for (int i = 0; !cancel && (i < countFilesInDir); i++) {
            currentFileIndex = i + 1;
            fileInDir        = filesInDir[i];
            isDelete         = !imageFilesExisting.contains(fileInDir);

            if (isDelete && fileInDir.isFile()) {
                logDelete(fileInDir);

                if (fileInDir.delete()) {
                    countDeleted++;

                    if (tnPanel.containsFile(fileInDir)) {
                        tnPanel.remove(Arrays.asList(fileInDir));
                    }
                } else {
                    AppLogger.logWarning(
                        getClass(), "DeleteOrphanedThumbnails.Error.Delete",
                        fileInDir);
                }
            }

            notifyPerformed(fileInDir);
        }

        notifyEnded();
    }

    private synchronized void logDelete(File file) {
        AppLogger.logInfo(DeleteOrphanedThumbnails.class,
                          "DeleteOrphanedThumbnails.Info.DeleteFile", file);
    }

    private synchronized void notifyStarted() {
        AppLogger.logInfo(DeleteOrphanedThumbnails.class,
                          "DeleteOrphanedThumbnails.Info.Start",
                          countFilesInDir);

        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir, 0,
                                getStartMessage());

        ls.notifyStarted(evt);
    }

    private void notifyPerformed(File file) {
        AppLogger.logFinest(DeleteOrphanedThumbnails.class,
                            "DeleteOrphanedThumbnails.Info.Performed", file);

        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir,
                                currentFileIndex, getPerformedMessage(file));

        ls.notifyPerformed(evt);
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir,
                                currentFileIndex, getEndMessage());

        ls.notifyEnded(evt);
        AppLogger.logInfo(DeleteOrphanedThumbnails.class,
                          "DeleteOrphanedThumbnails.Info.End",
                          currentFileIndex);
    }

    private String getStartMessage() {
        return JptBundle.INSTANCE.getString(
            "DeleteOrphanedThumbnails.Info.Start", countFilesInDir);
    }

    private String getPerformedMessage(File file) {
        return JptBundle.INSTANCE.getString(
            "DeleteOrphanedThumbnails.Info.Performed", file);
    }

    private String getEndMessage() {
        return JptBundle.INSTANCE.getString(
            "DeleteOrphanedThumbnails.Info.End", countDeleted);
    }
}
