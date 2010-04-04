/*
 * @(#)ControllerCopyOrCutFilesToClipboard.java    Created on 2008-10-26
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

import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Toolkit;

import javax.swing.JMenuItem;

/**
 * Listens to {@link PopupMenuThumbnails#getItemCopyToClipboard()},
 * {@link PopupMenuThumbnails#getItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 *
 * Enables or disables that menu items based on selection.
 *
 * @author  Elmar Baumann
 */
public final class ControllerCopyOrCutFilesToClipboard
        implements ActionListener, KeyListener, ThumbnailsPanelListener {
    private final ThumbnailsPanel thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final PopupMenuThumbnails popup = PopupMenuThumbnails.INSTANCE;
    private final JMenuItem           menuItemCopy =
        popup.getItemCopyToClipboard();
    private final JMenuItem           menuItemCut =
        popup.getItemCutToClipboard();

    public ControllerCopyOrCutFilesToClipboard() {
        listen();
    }

    private void listen() {
        menuItemCopy.addActionListener(this);
        menuItemCut.addActionListener(this);
        thumbnailsPanel.addThumbnailsPanelListener(this);
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            return;
        }

        if (KeyEventUtil.isMenuShortcut(e, KeyEvent.VK_C)) {
            perform(FileAction.COPY);
        } else if (KeyEventUtil.isMenuShortcut(e, KeyEvent.VK_X)) {
            perform(FileAction.CUT);
        }
    }

    private void perform(FileAction fa) {
        thumbnailsPanel.setFileAction(fa);
        transferSelectedFiles();
        popup.getItemPasteFromClipboard().setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            setFileAction(e.getSource());
            transferSelectedFiles();
            popup.getItemPasteFromClipboard().setEnabled(true);
        }
    }

    public void setFileAction(Object source) {
        if (source == menuItemCopy) {
            thumbnailsPanel.setFileAction(FileAction.COPY);
        } else if (source == menuItemCut) {
            thumbnailsPanel.setFileAction(FileAction.CUT);
        } else {
            assert false : "Invalid source: " + source;
        }
    }

    private void transferSelectedFiles() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        ClipboardUtil.copyToClipboard(thumbnailsPanel.getSelectedFiles(),
                                      clipboard, null);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        final boolean imagesSelected = thumbnailsPanel.getSelectionCount() > 0;

        menuItemCopy.setEnabled(imagesSelected);

        // ignore possibility of write protected files
        menuItemCut.setEnabled(imagesSelected);
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }

    @Override
    public void keyTyped(KeyEvent e) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // ignore
    }
}
