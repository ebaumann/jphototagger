/*
 * @(#)BackupDatabase.java    Created on 2010-03-08
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.ProgressBarUpdater;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.io.FileUtil;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class BackupDatabase extends AppLifeCycle.FinalTask
        implements Runnable {
    public static final BackupDatabase INSTANCE         = new BackupDatabase();
    volatile int                       currentFileIndex = 0;
    volatile int                       filecount        = 0;
    volatile boolean                   cancel;
    ProgressBarUpdater                 progressBarUpdater;

    private BackupDatabase() {}

    @Override
    public void run() {
        backup();
        notifyFinished();
    }

    public void cancel() {
        cancel = true;
    }

    @Override
    public void execute() {
        Thread thread = new Thread(INSTANCE);

        thread.setName("Backup database @ "
                       + BackupDatabase.class.getSimpleName());
        thread.start();
    }

    private void backup() {
        List<File> dbFiles = getDbFiles();
        List<File> tnFiles = getTnFiles();

        if (!dbFiles.isEmpty()) {
            File backupDir = getBackupDir();

            if (backupDir == null) {
                return;
            }

            File tnBackupDir =
                new File(backupDir.getAbsolutePath() + File.separator
                         + UserSettings.getThumbnailDirBasename());

            progressBarUpdater = new ProgressBarUpdater(
                JptBundle.INSTANCE.getString(
                    "BackupDatabase.ProgressBar.String"));
            filecount = dbFiles.size() + tnFiles.size();
            notifyProgressStarted();

            if (backup(dbFiles, backupDir) &&!cancel) {
                backup(tnFiles, tnBackupDir);
            }

            notifyProgressEnded();
        } else {
            MessageDisplayer.error(null, "BackupDatabase.Error.FileNotExists");
        }
    }

    private boolean backup(List<File> files, File toDir) {
        for (File file : files) {
            try {
                FileUtil.copyFile(file,
                                  new File(toDir + File.separator
                                           + file.getName()));
                currentFileIndex++;
                notifyProgressPerformed();

                if (cancel) {
                    return true;
                }
            } catch (IOException ex) {
                AppLogger.logSevere(BackupDatabase.class, ex);
                MessageDisplayer.error(null, "BackupDatabase.Error.Copy", file,
                                       toDir);

                return false;
            }
        }

        return true;
    }

    private List<File> getDbFiles() {
        File dbDir =
            new File(UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
        String dbPattern = Pattern.quote(UserSettings.getDatabaseBasename())
                           + ".*";
        File[] dbFileArray = dbDir.listFiles(new RegexFileFilter(dbPattern,
                                 ""));

        if (dbFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(dbFileArray);
    }

    private List<File> getTnFiles() {
        String tnPattern = ".*\\.jpeg";
        File   tnDir     =
            new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());
        File[] tnFileArray = tnDir.listFiles(new RegexFileFilter(tnPattern,
                                 ""));

        if (tnFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(tnFileArray);
    }

    private File getBackupDir() {
        DateFormat df      = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
        String     dirname =
            UserSettings.INSTANCE.getDatabaseBackupDirectoryName()
            + File.separator + df.format(new Date());
        File dir   = new File(dirname);
        File tnDir = new File(dirname + File.separator
                              + UserSettings.getThumbnailDirBasename());

        if (dir.mkdir() && tnDir.mkdir()) {
            return dir;
        } else {
            MessageDisplayer.error(null, "BackupDatabase.Error.CreateDir", dir);

            return null;
        }
    }

    private void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount, 0, null);

        progressBarUpdater.progressStarted(evt);
    }

    private void notifyProgressPerformed() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount,
                                currentFileIndex, null);

        progressBarUpdater.progressPerformed(evt);
    }

    private void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount,
                                currentFileIndex, null);

        progressBarUpdater.progressEnded(evt);
    }
}
