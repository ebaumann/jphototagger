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
import de.elmar_baumann.jpt.types.Filename;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-07
 */
public final class ControllerBackupDatabase extends Controller {
    private static final String KEY_LAST_DIR =
        "ControllerBackupDatabase.LastDirectory";
    private static final BackupDb BACKUP_TASK      = new BackupDb();
    private final JMenuItem       menuItemBackupDb =
        GUI.INSTANCE.getAppFrame().getMenuItemBackupDatabase();
    private File lastDir =
        new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIR));

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
        selectBackupDirAndAddTask();
    }

    @Override
    protected void action(KeyEvent evt) {

        // Ignore
    }

    private void selectBackupDirAndAddTask() {
        AppFrame appFrame = GUI.INSTANCE.getAppFrame();

        if (MessageDisplayer.confirmYesNo(
                appFrame, "ControllerBackupDatabase.Info.ChooseDir")) {
            DirectoryChooser dlg =
                new DirectoryChooser(
                    appFrame, lastDir,
                    UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

            dlg.addWindowListener(new SizeAndLocationController());
            dlg.setVisible(true);

            if (dlg.accepted()) {
                lastDir = dlg.getSelectedDirectories().get(0);
                writeLastDirToProperties();
                BACKUP_TASK.setBackupDir(lastDir);
                AppLifeCycle.INSTANCE.addFinalTask(BACKUP_TASK);
            }
        }
    }

    private void writeLastDirToProperties() {
        UserSettings.INSTANCE.getSettings().set(lastDir.getAbsolutePath(),
                KEY_LAST_DIR);
        UserSettings.INSTANCE.writeToFile();
    }

    private static class BackupDb extends AppLifeCycle.FinalTask {
        private File backupDir = new File("");

        @Override
        public void execute() {
            backup();
            notifyFinished();
        }

        public void setBackupDir(File dir) {
            assert dir.isDirectory() : dir;
            backupDir = dir;
        }

        private void backup() {
            File db = getDbFile();

            if (db.exists()) {
                File backupFile = getBackupFile();
                JProgressBar progressBar = ProgressBar.INSTANCE.getResource(this);

                setProgressBarStarted(progressBar);
                try {
                    FileUtil.copyFile(db, backupFile);
                } catch (IOException ex) {
                    MessageDisplayer.error(null, "BackupDb.Error.Copy", db,
                                           backupFile);
                }
                setProgressBarEnded(progressBar);
            } else {
                MessageDisplayer.error(null, "BackupDb.Error.FileNotExists",
                                       db);
            }
        }

        private File getDbFile() {
            return new File(
                UserSettings.INSTANCE.getDatabaseFileName(Filename.FULL_PATH));
        }

        private File getBackupFile() {
            String basename = backupDir.getAbsolutePath() + File.separator
                              + new File(
                                  UserSettings.INSTANCE.getDatabaseFileName(
                                      Filename.FULL_PATH)).getName();
            DateFormat df = new SimpleDateFormat("-yyyy-MM-dd");

            return FileUtil.getNotExistingFile(new File(basename
                    + df.format(new Date())));
        }

        private void setProgressBarStarted(JProgressBar progressBar) {
            if (progressBar != null) {
                progressBar.setIndeterminate(true);
                progressBar.setStringPainted(true);
                progressBar.setString(JptBundle.INSTANCE.getString("BackupDb.ProgressBar.String"));
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
