/*
 * @(#)ControllerPasteFilesFromClipboard.java    Created on 2008-10-27
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

import java.awt.EventQueue;
import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.datatransfer.TransferHandlerDirectoryTree;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.TransferHandler;

/**
 * Listens to {@link PopupMenuThumbnails#getItemPasteFromClipboard()} and on action
 * performed this class pastes the images in the clipboard into the current
 * directory.
 *
 * Enables the menu items based on the content (when it's a single directory).
 *
 * @author  Elmar Baumann
 */
public final class ControllerPasteFilesFromClipboard
        implements ActionListener, KeyListener, MenuListener,
                   ThumbnailsPanelListener {
    private final ThumbnailsPanel tnPanel = ViewUtil.getThumbnailsPanel();
    private final PopupMenuThumbnails popup         =
        PopupMenuThumbnails.INSTANCE;
    private final JMenuItem           menuItemPaste =
        popup.getItemPasteFromClipboard();

    public ControllerPasteFilesFromClipboard() {
        listen();
    }

    private void listen() {
        menuItemPaste.addActionListener(this);
        tnPanel.addThumbnailsPanelListener(this);
        GUI.INSTANCE.getAppFrame().getMenuEdit().addMenuListener(this);
        tnPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (!menuItemPaste.isEnabled()) {
            return;
        }

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_V)
                && canPasteFiles()) {
            Object source = evt.getSource();

            if (source == tnPanel) {
                insertFiles(getDirectory());
            } else if (isTreeSelection(source)) {
                insertFiles(ViewUtil.getSelectedFile((JTree) source));
            }
        }
    }

    private boolean isTreeSelection(Object source) {
        if (source instanceof JTree) {
            return ((JTree) source).getSelectionCount() > 0;
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (tnPanel.getContent().canInsertImagesFromFileSystem()) {
            insertFiles(getDirectory());
            menuItemPaste.setEnabled(false);
        }
    }

    private File getDirectory() {
        Content content = tnPanel.getContent();

        if (content.equals(Content.DIRECTORY)) {
            return ViewUtil.getSelectedFile(
                GUI.INSTANCE.getAppPanel().getTreeDirectories());
        } else if (content.equals(Content.FAVORITE)) {
            return ViewUtil.getSelectedFile(
                GUI.INSTANCE.getAppPanel().getTreeFavorites());
        }

        return null;
    }

    private void insertFiles(final File file) {
        if ((file == null) ||!file.isDirectory()) {
            return;
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<File> files = ClipboardUtil.getFilesFromSystemClipboard(
                                       FilenameDelimiter.NEWLINE);

                TransferHandlerDirectoryTree.handleDroppedFiles(
                    getEstimatedTransferHandlerAction(), files, file);
                emptyClipboard();
            }
            public int getEstimatedTransferHandlerAction() {
                Integer action =
                    tnPanel.getFileAction().getTransferHandlerAction();

                return (action == null)
                       ? TransferHandler.COPY
                       : action;
            }
            private void emptyClipboard() {
                ClipboardUtil.copyToSystemClipboard(new ArrayList<File>(),
                        null);
            }
        });
    }

    @Override
    public void thumbnailsSelectionChanged() {

        // ignore
    }

    @Override
    public void thumbnailsChanged() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
        menuItemPaste.setEnabled(canPasteFiles());
    }
        });
    }

    private boolean canPasteFiles() {
        return tnPanel.getContent().canInsertImagesFromFileSystem()
               && TransferUtil.systemClipboardMaybeContainFiles();
    }

    @Override
    public void menuSelected(MenuEvent evt) {
        menuItemPaste.setEnabled(canPasteFiles());
    }

    @Override
    public void menuDeselected(MenuEvent evt) {

        // ignore
    }

    @Override
    public void menuCanceled(MenuEvent evt) {

        // ignore
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
