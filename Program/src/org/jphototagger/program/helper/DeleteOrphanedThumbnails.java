/*
 * @(#)DeleteOrphanedThumbnails.java    Created on 2009-10-17
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.Arrays;
import java.util.Set;
import org.jphototagger.lib.concurrent.Cancelable;

/**
 * Deletes thumbnails without an image file in the database.
 * <p>
 * Uses {@link DatabaseImageFiles#getAllThumbnailFiles()} to determine valid
 * thumbnail files. Then deletes all other files in the directory
 * {@link UserSettings#getThumbnailsDirectoryName()}.
 *
 * @author  Elmar Baumann
 */
public final class DeleteOrphanedThumbnails implements Runnable, Cancelable {
    private final ProgressListenerSupport listenerSupport =
        new ProgressListenerSupport();
    private int              countFilesInDir  = 0;
    private int              countDeleted     = 0;
    private int              currentFileIndex = 0;
    private volatile boolean cancelled        = false;

    public synchronized void addProgressListener(ProgressListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        listenerSupport.add(l);
    }

    /**
     * A <em>soft</em> interrupt: I/O operations can finishing their current
     * process.
     */
    @Override
    public synchronized void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        Set<File> imageFilesExisting =
            DatabaseImageFiles.INSTANCE.getAllThumbnailFiles();
        File[] filesInDir =
            new File(
                UserSettings.INSTANCE.getThumbnailsDirectoryName()).listFiles();
        ThumbnailsPanel tnPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
        boolean isDelete  = false;
        File    fileInDir = null;

        countFilesInDir = filesInDir.length;
        notifyStarted();

        for (int i = 0; !cancelled && (i < countFilesInDir); i++) {
            currentFileIndex = i + 1;
            fileInDir        = filesInDir[i];
            isDelete         = !imageFilesExisting.contains(fileInDir);

            if (isDelete && fileInDir.isFile()) {
                logDelete(fileInDir);

                if (fileInDir.delete()) {
                    countDeleted++;

                    if (tnPanel.displaysFile(fileInDir)) {
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

        listenerSupport.notifyStarted(evt);
    }

    private void notifyPerformed(File file) {
        AppLogger.logFinest(DeleteOrphanedThumbnails.class,
                            "DeleteOrphanedThumbnails.Info.Performed", file);

        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir,
                                currentFileIndex, getPerformedMessage(file));

        listenerSupport.notifyPerformed(evt);
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, countFilesInDir,
                                currentFileIndex, getEndMessage());

        listenerSupport.notifyEnded(evt);
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
