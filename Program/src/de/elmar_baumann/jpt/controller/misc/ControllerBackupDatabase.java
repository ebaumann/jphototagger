/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.io.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-07
 */
public final class ControllerBackupDatabase extends Controller {
    private static final BackupDb BACKUP_TASK      = new BackupDb();
    private final JMenuItem       menuItemBackupDb =
        GUI.INSTANCE.getAppFrame().getMenuItemBackupDatabase();

    public ControllerBackupDatabase() {
        listenToActionsOf(menuItemBackupDb);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == menuItemBackupDb;
    }

    @Override
    protected void action(ActionEvent evt) {
        addBackupTask();
    }

    @Override
    protected void action(KeyEvent evt) {

        // Ignore
    }

    private void addBackupTask() {
        MessageDisplayer.information(null,
                                     "ControllerBackupDatabase.Info.ChooseDir");
        AppLifeCycle.INSTANCE.addFinalTask(BACKUP_TASK);
    }

    private static class BackupDb extends AppLifeCycle.FinalTask
            implements Runnable {
        @Override
        public void run() {
            backup();
            notifyFinished();
        }

        @Override
        public void execute() {
            Thread thread = new Thread(BACKUP_TASK);

            thread.setName("Backup database @ "
                           + BackupDb.class.getSimpleName());
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
                JProgressBar progressBar =
                    ProgressBar.INSTANCE.getResource(this);

                setProgressBarStarted(progressBar);

                if (backup(dbFiles, backupDir)) {
                    backup(tnFiles, tnBackupDir);
                }

                setProgressBarEnded(progressBar);
            } else {
                MessageDisplayer.error(null, "BackupDb.Error.FileNotExists");
            }
        }

        private boolean backup(List<File> files, File toDir) {
            for (File file : files) {
                try {
                    FileUtil.copyFile(file,
                                      new File(toDir + File.separator
                                               + file.getName()));
                } catch (IOException ex) {
                    AppLogger.logSevere(BackupDb.class, ex);
                    MessageDisplayer.error(null, "BackupDb.Error.Copy", file,
                                           toDir);

                    return false;
                }
            }

            return true;
        }

        private List<File> getDbFiles() {
            File dbDir =
                new File(
                    UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
            String dbPattern =
                Pattern.quote(UserSettings.getDatabaseBasename()) + ".*";
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
                MessageDisplayer.error(null, "BackupDb.Error.CreateDir", dir);

                return null;
            }
        }

        private void setProgressBarStarted(JProgressBar progressBar) {
            if (progressBar != null) {
                progressBar.setIndeterminate(true);
                progressBar.setStringPainted(true);
                progressBar.setString(
                    JptBundle.INSTANCE.getString(
                        "BackupDb.ProgressBar.String"));
            }
        }

        private void setProgressBarEnded(JProgressBar progressBar) {
            if (progressBar != null) {
                progressBar.setIndeterminate(false);
                ProgressBar.INSTANCE.releaseResource(this);
            }
        }
    }
}
