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
package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.FileAction;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JMenuItem;

/**
 * Listens to {@link PopupMenuThumbnails#getItemCopyToClipboard()},
 * {@link PopupMenuThumbnails#getItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 *
 * Enables or disables that menu items based on selection.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class ControllerCopyOrCutFilesToClipboard
        implements ActionListener, KeyListener, ThumbnailsPanelListener {

    private final ThumbnailsPanel     thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final PopupMenuThumbnails popup           = PopupMenuThumbnails.INSTANCE;
    private final JMenuItem           menuItemCopy    = popup.getItemCopyToClipboard();
    private final JMenuItem           menuItemCut     = popup.getItemCutToClipboard();

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
        if (thumbnailsPanel.getSelectionCount() <= 0) return;
        if (KeyEventUtil.isControl(e, KeyEvent.VK_C)) {
            perform(FileAction.COPY);
        } else if (KeyEventUtil.isControl(e, KeyEvent.VK_X)) {
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
        ClipboardUtil.copyToClipboard(thumbnailsPanel.getSelectedFiles(), clipboard, null);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        final boolean imagesSelected = thumbnailsPanel.getSelectionCount() > 0;
        menuItemCopy.setEnabled(imagesSelected);
        menuItemCut.setEnabled(imagesSelected); // ignore possibility of write protected files
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
