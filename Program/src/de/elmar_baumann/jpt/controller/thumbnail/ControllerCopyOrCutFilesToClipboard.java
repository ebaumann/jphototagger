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
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Listens to {@link AppFrame#getMenuItemCopyToClipboard()},
 * {@link AppFrame#getMenuItemCutToClipboard()} and on action
 * performed this class copies or cuts the selected files into the clipboard.
 * 
 * Enables or disables that menu items based on selection.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class ControllerCopyOrCutFilesToClipboard
        implements ActionListener, ThumbnailsPanelListener {

    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem menuItemCopy =
            GUI.INSTANCE.getAppFrame().getMenuItemCopyToClipboard();
    private final JMenuItem menuItemCut =
            GUI.INSTANCE.getAppFrame().getMenuItemCutToClipboard();

    public ControllerCopyOrCutFilesToClipboard() {
        listen();
    }

    private void listen() {
        menuItemCopy.addActionListener(this);
        menuItemCut.addActionListener(this);
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            setFileAction(e.getSource());
            transferSelectedFiles();
            GUI.INSTANCE.getAppFrame().getMenuItemPasteFromClipboard().
                    setEnabled(
                    true);
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
        Clipboard clipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();
        ClipboardUtil.copyToClipboard(thumbnailsPanel.getSelectedFiles(),
                clipboard, null);
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
}
