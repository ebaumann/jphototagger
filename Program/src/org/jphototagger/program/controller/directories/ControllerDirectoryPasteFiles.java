/*
 * @(#)ControllerDirectoryPasteFiles.java    Created on 2008-10-26
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

package org.jphototagger.program.controller.directories;

import org.jphototagger.program.datatransfer.TransferHandlerDirectoryTree;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.ViewUtil;
import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.event.util.KeyEventUtil;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.List;

import javax.swing.JTree;

/**
 * Listens to keyboard actions whithin the directories tree and copies or
 * moves files to directories when the keys related to a copy or move action.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDirectoryPasteFiles implements KeyListener {
    private final AppPanel        appPanel        = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel =
        appPanel.getPanelThumbnails();

    public ControllerDirectoryPasteFiles() {
        listen();
    }

    private void listen() {
        appPanel.getTreeDirectories().addKeyListener(this);
        appPanel.getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isPaste(evt)) {
            Object source = evt.getSource();

            if (source instanceof JTree) {
                copyOrMovePastedFilesTo((JTree) source);
            }
        }
    }

    private void copyOrMovePastedFilesTo(JTree targetTree) {
        if (isSingleDirectory(thumbnailsPanel.getContent())
                && filesWereCopiedOrCutted(thumbnailsPanel.getFileAction())) {
            insertFilesIntoSelectedDirectoryOf(targetTree);
        }
    }

    private void insertFilesIntoSelectedDirectoryOf(JTree targetTree) {
        List<File> sourceFiles = ClipboardUtil.getFilesFromSystemClipboard(
                                     FilenameDelimiter.NEWLINE);
        File targetDirectory = ViewUtil.getSelectedFile(targetTree);

        if ((targetDirectory != null) &&!sourceFiles.isEmpty()) {
            copyOrMoveFiles(sourceFiles, targetDirectory);
        }
    }

    private void copyOrMoveFiles(List<File> sourceFiles, File targetDirectory) {
        FileAction action = thumbnailsPanel.getFileAction();

        if (filesWereCopiedOrCutted(action)) {
            TransferHandlerDirectoryTree.handleDroppedFiles(
                action.getTransferHandlerAction(), sourceFiles,
                targetDirectory);
            thumbnailsPanel.setFileAction(FileAction.UNDEFINED);
        }
    }

    private boolean filesWereCopiedOrCutted(FileAction action) {
        return action.equals(FileAction.COPY) || action.equals(FileAction.CUT);
    }

    private boolean isSingleDirectory(Content content) {
        return content.equals(Content.DIRECTORY)
               || content.equals(Content.FAVORITE);
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }
}
