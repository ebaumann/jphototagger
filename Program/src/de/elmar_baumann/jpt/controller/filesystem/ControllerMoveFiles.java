/*
 * @(#)ControllerMoveFiles.java    Created on 2008-10-13
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

package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;

/**
 * Renames files in the file system.
 *
 * @author  Elmar Baumann
 */
public final class ControllerMoveFiles
        implements ActionListener, FileSystemListener {
    private final ThumbnailsPanel thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    public ControllerMoveFiles() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemMoveFiles()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSelectedFiles();
    }

    private void moveSelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();

        if (files.size() > 0) {
            MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();

            dialog.setSourceFiles(files);
            dialog.addFileSystemListener(this);
            dialog.setVisible(true);
        } else {
            AppLogger.logWarning(
                ControllerMoveFiles.class,
                "ControllerMoveFiles.ErrorMessaga.NoImagesSelected");
        }
    }

    private boolean isXmpFile(File file) {
        return file.getName().toLowerCase().endsWith("xmp");
    }

    @Override
    public void fileMoved(File source, File target) {
        if (!isXmpFile(source)) {
            db.updateRename(source.getAbsolutePath(), target.getAbsolutePath());
        }
    }

    @Override
    public void fileCopied(File source, File target) {

        // ignore
    }

    @Override
    public void fileDeleted(File file) {

        // ignore
    }

    @Override
    public void fileRenamed(File oldFile, File newFile) {

        // ignore
    }
}
