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
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.io.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
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
            File[] dbFiles = getDbFiles();

            if ((dbFiles != null) && (dbFiles.length > 0)) {
                File backupDir = getBackupDir();

                if (backupDir == null) {
                    return;
                }

                JProgressBar progressBar =
                    ProgressBar.INSTANCE.getResource(this);

                setProgressBarStarted(progressBar);

                for (File dbFile : dbFiles) {
                    try {
                        FileUtil.copyFile(dbFile,
                                          new File(backupDir + File.separator
                                                   + dbFile.getName()));
                    } catch (IOException ex) {
                        AppLogger.logSevere(BackupDb.class, ex);
                        MessageDisplayer.error(null, "BackupDb.Error.Copy",
                                               dbFile, backupDir);

                        break;
                    }
                }

                setProgressBarEnded(progressBar);
            } else {
                MessageDisplayer.error(null, "BackupDb.Error.FileNotExists");
            }
        }

        private File[] getDbFiles() {
            File dbDir =
                new File(
                    UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
            String pattern = Pattern.quote(UserSettings.getDatabaseBasename())
                             + ".*";

            return dbDir.listFiles(new RegexFileFilter(pattern, ""));
        }

        private File getBackupDir() {
            DateFormat df      = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
            String     dirname =
                UserSettings.INSTANCE.getDatabaseBackupDirectoryName()
                + File.separator + df.format(new Date());
            File dir = new File(dirname);

            if (dir.mkdir()) {
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
