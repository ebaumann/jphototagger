/*
 * @(#)ControllerDeleteThumbnailsFromDatabase.java    Created on 2008-09-10
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

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteThumbnailsFromDatabase
        implements ActionListener {
    private final DatabaseImageFiles  db              =
        DatabaseImageFiles.INSTANCE;
    private final PopupMenuThumbnails popupMenu       =
        PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel     thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerDeleteThumbnailsFromDatabase() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDeleteImageFromDatabase().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteSelectedThumbnails();
    }

    private void deleteSelectedThumbnails() {
        if (confirmDelete()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    List<File> files        =
                        thumbnailsPanel.getSelectedFiles();
                    int        countFiles   = files.size();
                    int        countDeleted = db.delete(files);

                    if (countDeleted != countFiles) {
                        errorMessageDeleteImageFiles(countFiles, countDeleted);
                    }

                    repaint(files);
                    thumbnailsPanel.repaint();
                }
            });
        }
    }

    private void repaint(final List<File> files) {
        List<File> deleted = new ArrayList<File>(files.size());

        for (File file : files) {
            if (!db.exists(file)) {
                deleted.add(file);
            }
        }

        thumbnailsPanel.remove(deleted);
    }

    private boolean confirmDelete() {
        return MessageDisplayer.confirmYesNo(
            null,
            "ControllerDeleteThumbnailsFromDatabase.Confirm.DeleteSelectedFiles",
            thumbnailsPanel.getSelectionCount());
    }

    private void errorMessageDeleteImageFiles(int countFiles,
            int countDeleted) {
        MessageDisplayer.error(
            null,
            "ControllerDeleteThumbnailsFromDatabase.Error.DeleteSelectedFiles",
            countFiles, countDeleted);
    }
}
