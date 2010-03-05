/*
 * JPhotoTagger tags and finds images fast
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
import de.elmar_baumann.jpt.event.FileSystemEvent;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public final class ControllerMoveFiles implements ActionListener, FileSystemListener {

    private final ThumbnailsPanel    thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db              = DatabaseImageFiles.INSTANCE;

    public ControllerMoveFiles() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemMoveFiles().addActionListener(this);
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
            AppLogger.logWarning(ControllerMoveFiles.class, "ControllerMoveFiles.ErrorMessaga.NoImagesSelected");
        }
    }

    @Override
    public void actionPerformed(FileSystemEvent event) {

        if (!event.getType().equals(FileSystemEvent.Type.MOVE) || event.isError()) return;

        File src    = event.getSource();
        File target = event.getTarget();

        if (!src.getName().toLowerCase().endsWith(".xmp")) {
            db.updateRename(src.getAbsolutePath(), target.getAbsolutePath());
        }
    }
}
