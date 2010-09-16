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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteThumbnailsFromDatabase
        implements ActionListener {
    public ControllerDeleteThumbnailsFromDatabase() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemDeleteImageFromDatabase()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        deleteSelectedThumbnails();
    }

    private void deleteSelectedThumbnails() {
        if (confirmDelete()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    List<File> selFiles   = ViewUtil.getSelectedImageFiles();
                    int        countFiles = selFiles.size();
                    int        countDeleted =
                        DatabaseImageFiles.INSTANCE.delete(selFiles);

                    if (countDeleted != countFiles) {
                        errorMessageDeleteImageFiles(countFiles, countDeleted);
                    }

                    repaint(selFiles);
                    ViewUtil.getThumbnailsPanel().repaint();
                }
            });
        }
    }

    private void repaint(final List<File> files) {
        List<File>         deleted = new ArrayList<File>(files.size());
        DatabaseImageFiles db      = DatabaseImageFiles.INSTANCE;

        for (File file : files) {
            if (!db.exists(file)) {
                deleted.add(file);
            }
        }

        ViewUtil.getThumbnailsPanel().remove(deleted);
    }

    private boolean confirmDelete() {
        return MessageDisplayer.confirmYesNo(
            null,
            "ControllerDeleteThumbnailsFromDatabase.Confirm.DeleteSelectedFiles",
            ViewUtil.getThumbnailsPanel().getSelectionCount());
    }

    private void errorMessageDeleteImageFiles(int countFiles,
            int countDeleted) {
        MessageDisplayer.error(
            null,
            "ControllerDeleteThumbnailsFromDatabase.Error.DeleteSelectedFiles",
            countFiles, countDeleted);
    }
}
