/*
 * @(#)ControllerCopyFilesToDirectory.java    Created on 2008-10-05
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.helper.FilesystemDatabaseUpdater;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author Elmar Baumann
 */
public final class ControllerCopyFilesToDirectory implements ActionListener {
    public ControllerCopyFilesToDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemCopyToDirectory()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        copySelectedFiles();
    }

    private void copySelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (!selFiles.isEmpty()) {
            CopyToDirectoryDialog dlg = new CopyToDirectoryDialog();

            dlg.setSourceFiles(selFiles);
            dlg.addFileSystemActionListener(
                new FilesystemDatabaseUpdater(true));
            dlg.setVisible(true);
        } else {
            AppLogger.logWarning(
                ControllerCopyFilesToDirectory.class,
                "ControllerCopyFilesToDirectory.Error.NoImagesSelected");
        }
    }
}
