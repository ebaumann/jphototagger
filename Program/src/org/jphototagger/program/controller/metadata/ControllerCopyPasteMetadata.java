/*
 * @(#)ControllerCopyPasteMetadata.java    Created on 2009-08-07
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

package org.jphototagger.program.controller.metadata;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import javax.swing.JMenuItem;

/**
 * Listens to the menu items {@link PopupMenuThumbnails#getItemCopyMetadata()} and
 * {@link PopupMenuThumbnails#getItemPasteMetadata()} and on action performed copies
 * XMP metadata of the {@link EditMetadataPanels} or paste it via
 * {@link EditMetadataPanels#getXmp()} or
 * {@link EditMetadataPanels#setXmp(org.jphototagger.program.data.Xmp)}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerCopyPasteMetadata
        implements ActionListener, KeyListener, ThumbnailsPanelListener {
    private Xmp xmp;

    public ControllerCopyPasteMetadata() {
        listen();
    }

    private void listen() {
        getCopyItem().addActionListener(this);
        getPasteItem().addActionListener(this);
        ViewUtil.getThumbnailsPanel().addThumbnailsPanelListener(this);
        ViewUtil.getThumbnailsPanel().addKeyListener(this);
    }

    private JMenuItem getCopyItem() {
        return PopupMenuThumbnails.INSTANCE.getItemCopyMetadata();
    }

    private JMenuItem getPasteItem() {
        return PopupMenuThumbnails.INSTANCE.getItemPasteMetadata();
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcutWithShiftDown(evt, KeyEvent.VK_C)) {
            copy();
        } else if (KeyEventUtil.isMenuShortcutWithShiftDown(evt,
                KeyEvent.VK_V)) {
            paste();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == getCopyItem()) {
            copy();
        } else if (evt.getSource() == getPasteItem()) {
            paste();
        }
    }

    private void copy() {
        this.xmp = new Xmp(
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels().getXmp());
        getPasteItem().setEnabled(true);
    }

    private void paste() {
        if (xmp == null) {
            return;
        }

        EditMetadataPanels editPanel =
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels();

        if (!checkSelected() ||!checkCanEdit(editPanel)) {
            return;
        }

        editPanel.setXmp(xmp);
        getPasteItem().setEnabled(false);
        xmp = null;
    }

    private boolean checkSelected() {
        int selCount = ViewUtil.getThumbnailsPanel().getSelectionCount();

        if (selCount <= 0) {
            MessageDisplayer.error(
                null, "ControllerCopyPasteMetadata.Error.NoSelection");

            return false;
        }

        return true;
    }

    private boolean checkCanEdit(EditMetadataPanels editPanel) {
        if (!editPanel.isEditable()) {
            MessageDisplayer.error(
                null, "ControllerCopyPasteMetadata.Error.NotEditable");

            return false;
        }

        return true;
    }

    @Override
    public void thumbnailsSelectionChanged() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                getCopyItem().setEnabled(
                    ViewUtil.getThumbnailsPanel().isFileSelected());
    }
        });
    }

    @Override
    public void thumbnailsChanged() {

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
