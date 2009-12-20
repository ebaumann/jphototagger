/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Deletes thumbnails without an image file in the database.
 * <p>
 * Uses {@link DatabaseImageFiles#getAllThumbnailFiles()} to determine valid
 * thumbnail files. Then deletes all other files in the directory
 * {@link UserSettings#getThumbnailsDirectoryName()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-10-17
 */
public final class DeleteOrphanedThumbnails implements Runnable {

    private final List<ProgressListener> listeners =
            new ArrayList<ProgressListener>();
    private int countFilesInDir = 0;
    private int countDeleted = 0;
    private int currentFileIndex = 0;
    private volatile boolean cancelled = false;

    public synchronized void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    public synchronized void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        Set<File> correctFiles = DatabaseImageFiles.INSTANCE.getAllThumbnailFiles();
        File[] filesInDir = new File(
                UserSettings.INSTANCE.getThumbnailsDirectoryName()).listFiles();
        countFilesInDir = filesInDir.length;
        notifyStarted();
        boolean isDelete = false;
        File fileInDir = null;
        for (int i = 0; !cancelled && i < countFilesInDir; i++) {
            currentFileIndex = i + 1;
            fileInDir = filesInDir[i];
            isDelete = !correctFiles.contains(fileInDir);
            if (isDelete && fileInDir.isFile()) {
                logDelete(fileInDir);
                fileInDir.delete();
                countDeleted++;
            }
            notifyPerformed(fileInDir);
        }
        notifyEnded();
    }

    private synchronized void logDelete(File file) {
        AppLog.logInfo(DeleteOrphanedThumbnails.class,
                "DeleteOrphanedThumbnails.Info.DeleteFile", file); // NOI18N
    }

    private synchronized void notifyStarted() {
        AppLog.logInfo(DeleteOrphanedThumbnails.class,
                "DeleteOrphanedThumbnails.Info.Start", countFilesInDir); // NOI18N
        ProgressEvent evt = new ProgressEvent(
                this, 0, countFilesInDir, 0, getStartMessage());
        for (ProgressListener listener : listeners) {
            listener.progressStarted(evt);
        }
    }

    private synchronized void notifyPerformed(File file) {
        AppLog.logFinest(DeleteOrphanedThumbnails.class,
                "DeleteOrphanedThumbnails.Info.Performed", file); // NOI18N
        ProgressEvent evt = new ProgressEvent(
                this, 0, countFilesInDir, currentFileIndex, getPerformedMessage(file));
        for (ProgressListener listener : listeners) {
            listener.progressPerformed(evt);
        }
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(
                this, 0, countFilesInDir, currentFileIndex, getEndMessage());
        for (ProgressListener listener : listeners) {
            listener.progressEnded(evt);
        }
        AppLog.logInfo(DeleteOrphanedThumbnails.class,
                "DeleteOrphanedThumbnails.Info.End", currentFileIndex); // NOI18N
    }

    private String getStartMessage() {
        return Bundle.getString("DeleteOrphanedThumbnails.Info.Start", countFilesInDir); // NOI18N
    }

    private String getPerformedMessage(File file) {
        return Bundle.getString("DeleteOrphanedThumbnails.Info.Performed", file); // NOI18N
    }

    private String getEndMessage() {
        return Bundle.getString("DeleteOrphanedThumbnails.Info.End", countDeleted); // NOI18N
    }
}
