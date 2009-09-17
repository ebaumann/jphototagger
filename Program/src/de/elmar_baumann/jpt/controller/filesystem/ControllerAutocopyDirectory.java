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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.AppTexts;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.helper.FilesystemDatabaseUpdater;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.jpt.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.SettingsMiscPanel;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter.Option;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.ListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-19
 */
public final class ControllerAutocopyDirectory implements ActionListener {

    private volatile boolean stop;

    public ControllerAutocopyDirectory() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemCopyFromAutocopyDirectory().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (stop) {
            AppLog.logWarning(ControllerAutocopyDirectory.class,
                    "ControllerAutocopyDirectory.Error.Stopped"); // NOI18N
        } else {
            copy();
        }
    }

    private void copy() {
        File dir = UserSettings.INSTANCE.getAutocopyDirectory();
        boolean dirOk = dir != null && confirmAutocopyDir(dir);

        if (dirOk) {
            copy(dir);
        } else if (confirmSetAutocopyDirectory(
                dir == null
                ? "ControllerAutocopyDirectory.Confirm.DefineDirectoryWasNull" // NOI18N
                : "ControllerAutocopyDirectory.Confirm.DefineDirectoryHasChanged")) { // NOI18N
            setAutocopyDirectory();
        }
    }

    private void setAutocopyDirectory() {
        UserSettingsDialog dialog = UserSettingsDialog.INSTANCE;
        dialog.selectTab(SettingsMiscPanel.Tab.AUTOCOPY_DIRECTORY);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    private void copy(File srcDir) {
        List<File> directories = new ArrayList<File>();
        directories.add(srcDir);
        directories.addAll(FileUtil.getSubdirectoriesRecursive(srcDir,
                new HashSet<Option>()));
        List<File> files = ImageFilteredDirectory.getImageFilesOfDirectories(
                directories);
        if (files.size() > 0) {
            CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
            dialog.setSourceFiles(files);
            new CopyTask(dialog).start();
            dialog.addFileSystemActionListener(
                    new FilesystemDatabaseUpdater(true));
            dialog.setVisible(true);
        } else {
            informationMessageNoFilesFound();
        }
    }

    private void informationMessageNoFilesFound() {
        if (MessageDisplayer.confirm(null,
                "ControllerAutocopyDirectory.Info.NoFilesFound", // NOI18N
                MessageDisplayer.CancelButton.HIDE).equals(
                MessageDisplayer.ConfirmAction.YES)) {
            setAutocopyDirectory();
            copy();
        }
    }

    private boolean confirmSetAutocopyDirectory(String bundleKey) {
        return MessageDisplayer.confirm(null,
                bundleKey,
                MessageDisplayer.CancelButton.HIDE).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private boolean confirmAutocopyDir(File dir) {
        return MessageDisplayer.confirm(null,
                "ControllerAutocopyDirectory.Confirm.AutocopyDir", // NOI18N
                MessageDisplayer.CancelButton.HIDE, dir).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private class CopyTask extends Thread implements ProgressListener {

        private final List<File> copiedFiles = new ArrayList<File>();
        private final CopyToDirectoryDialog dialog;

        public CopyTask(CopyToDirectoryDialog dialog) {
            this.dialog = dialog;
            setName("Auto copying directory @ " + // NOI18N
                    ControllerAutocopyDirectory.class.getName());
            setPriority(MIN_PRIORITY);
            dialog.addProgressListener(this);
            stop = true;
        }

        @Override
        public void progressStarted(ProgressEvent evt) {
            // ignore
        }

        @Override
        public void progressPerformed(ProgressEvent evt) {
            Object o = evt.getInfo();
            if (o instanceof Pair) {
                Pair pair = (Pair) o;
                Object fo = pair.getSecond();
                if (fo instanceof File) {
                    File file = (File) fo;
                    String filename = file.getName().toLowerCase();
                    if (!filename.endsWith(".xmp")) { // NOI18N
                        copiedFiles.add(file);
                    }
                }
            }
        }

        @Override
        public void progressEnded(ProgressEvent evt) {
            dialog.removeProgressListener(this);
            addFilesToCollection();
            stop = false;
        }

        private void addFilesToCollection() {
            if (copiedFiles.isEmpty()) return;
            ListModel listModel = GUI.INSTANCE.getAppPanel().
                    getListImageCollections().getModel();
            if (listModel instanceof ListModelImageCollections) {
                if (insertPrevCollectionIntoDb()) {
                    selectPrevImportCollection();
                }
            }
        }

        private boolean insertPrevCollectionIntoDb() {
            return DatabaseImageCollections.INSTANCE.insertImageCollection(
                    AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT,
                    FileUtil.getAsFilenames(copiedFiles));
        }

        private void selectPrevImportCollection() {
            AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            appPanel.getTabbedPaneSelection().setSelectedComponent(
                    appPanel.getTabSelectionImageCollections());
            GUI.INSTANCE.getAppPanel().getListImageCollections().
                    setSelectedValue(
                    AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT,
                    true);
        }
    }
}
