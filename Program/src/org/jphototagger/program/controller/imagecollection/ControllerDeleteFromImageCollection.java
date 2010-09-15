/*
 * @(#)ControllerDeleteFromImageCollection.java    Created on 2008-00-10
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

package org.jphototagger.program.controller.imagecollection;

import org.jphototagger.program.controller.filesystem.ControllerDeleteFiles;
import org.jphototagger.program.helper.ImageCollectionsHelper;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * image collection.
 *
 * @author  Elmar Baumann
 * @see     ControllerDeleteFiles
 */
public final class ControllerDeleteFromImageCollection
        implements ActionListener, KeyListener {
    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemDeleteFromImageCollection()
            .addActionListener(this);
        ViewUtil.getThumbnailsPanel().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        delete();
    }

    private void delete() {
        ThumbnailsPanel tnPanel = ViewUtil.getThumbnailsPanel();

        if (tnPanel.getContent().equals(Content.IMAGE_COLLECTION)
                && (tnPanel.isFileSelected())) {
            ImageCollectionsHelper.deleteSelectedFiles();
        }
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
