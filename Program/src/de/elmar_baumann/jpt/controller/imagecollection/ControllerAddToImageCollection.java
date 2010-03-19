/*
 * @(#)ControllerAddToImageCollection.java    Created on 2008-00-10
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

package de.elmar_baumann.jpt.controller.imagecollection;

import de.elmar_baumann.jpt.helper.ModifyImageCollections;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.ImageCollectionsDialog;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerAddToImageCollection implements ActionListener {
    private final PopupMenuThumbnails popupMenu       =
        PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel     thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerAddToImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemAddToImageCollection().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addSelectedFilesToImageCollection();
    }

    private void addSelectedFilesToImageCollection() {
        String collectionName = selectCollectionName();

        if (collectionName != null) {
            ModifyImageCollections.addImagesToCollection(
                collectionName,
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dialog = new ImageCollectionsDialog();

        dialog.setVisible(true);

        return dialog.getSelectedCollectionName();
    }
}
