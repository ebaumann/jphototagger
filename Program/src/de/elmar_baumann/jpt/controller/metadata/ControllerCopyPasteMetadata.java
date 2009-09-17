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
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Listens to the menu items {@link AppFrame#getMenuItemCopyMetadata()} and
 * {@link AppFrame#getMenuItemPasteMetadata()} and on action performed copies
 * XMP metadata of the {@link EditMetadataPanelsArray} or paste it via
 * {@link EditMetadataPanelsArray#getXmp()} or
 * {@link EditMetadataPanelsArray#setXmp(de.elmar_baumann.jpt.data.Xmp)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-07
 */
public final class ControllerCopyPasteMetadata
        implements ActionListener, ThumbnailsPanelListener {

    private final JMenuItem menuItemCopy =
            GUI.INSTANCE.getAppFrame().getMenuItemCopyMetadata();
    private final JMenuItem menuItemPaste =
            GUI.INSTANCE.getAppFrame().getMenuItemPasteMetadata();
    private Xmp xmp;

    public ControllerCopyPasteMetadata() {
        listen();
    }

    private void listen() {
        menuItemCopy.addActionListener(this);
        menuItemPaste.addActionListener(this);
        GUI.INSTANCE.getAppPanel().getPanelThumbnails().
                addThumbnailsPanelListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemCopy) {
            copy();
        } else if (e.getSource() == menuItemPaste) {
            paste();
        }
    }

    private void copy() {
        this.xmp = new Xmp(GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray().
                getXmp());
        menuItemPaste.setEnabled(true);
    }

    private void paste() {
        assert xmp != null : "xmp is null!"; // NOI18N
        if (xmp == null) return;
        EditMetadataPanelsArray editPanel =
                GUI.INSTANCE.getAppPanel().getMetadataEditPanelsArray();
        if (!checkSelected() || !checkCanEdit(editPanel)) return;
        editPanel.setXmp(xmp);
        menuItemPaste.setEnabled(false);
        xmp = null;
    }

    private boolean checkSelected() {
        int selCount = GUI.INSTANCE.getAppPanel().getPanelThumbnails().
                getSelectionCount();
        if (selCount <= 0) {
            MessageDisplayer.error(
                    null, "ControllerCopyPasteMetadata.Error.NoSelection"); // NOI18N
            return false;
        }
        return true;
    }

    private boolean checkCanEdit(EditMetadataPanelsArray editPanel) {
        if (!editPanel.isEditable()) {
            MessageDisplayer.error(
                    null, "ControllerCopyPasteMetadata.Error.NotEditable"); // NOI18N
            return false;
        }
        return true;
    }

    @Override
    public void thumbnailsSelectionChanged() {
        menuItemCopy.setEnabled(GUI.INSTANCE.getAppPanel().getPanelThumbnails().
                getSelectionCount() > 0);
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }
}
