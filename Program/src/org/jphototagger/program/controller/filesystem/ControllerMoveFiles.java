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

package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.MoveToDirectoryDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

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
    private final ThumbnailsPanel tnPanel =
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
    public void actionPerformed(ActionEvent evt) {
        moveSelectedFiles();
    }

    private void moveSelectedFiles() {
        List<File> selFiles = tnPanel.getSelectedFiles();

        if (!selFiles.isEmpty()) {
            MoveToDirectoryDialog dlg = new MoveToDirectoryDialog();

            dlg.setSourceFiles(selFiles);
            dlg.addFileSystemListener(this);
            dlg.setVisible(true);
        } else {
            AppLogger.logWarning(
                ControllerMoveFiles.class,
                "ControllerMoveFiles.ErrorMessaga.NoImagesSelected");
        }
    }

    /**
     * Moves files into a target directory without asking for confirmation.
     *
     * @param srcFiles  source files to move
     * @param targetDir target directory
     */
    public void moveFiles(List<File> srcFiles, File targetDir) {
        if (srcFiles == null) {
            throw new NullPointerException("srcFiles == null");
        }

        if (targetDir == null) {
            throw new NullPointerException("targetDir == null");
        }

        if (!srcFiles.isEmpty() && targetDir.isDirectory()) {
            MoveToDirectoryDialog dlg = new MoveToDirectoryDialog();

            dlg.setSourceFiles(srcFiles);
            dlg.setTargetDirectory(targetDir);
            dlg.addFileSystemListener(this);
            dlg.setVisible(true);
        }
    }

    private boolean isXmpFile(File file) {
        return file.getName().toLowerCase().endsWith("xmp");
    }

    @Override
    public void fileMoved(File source, File target) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        if (!isXmpFile(source)) {
            db.updateRename(source, target);
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
