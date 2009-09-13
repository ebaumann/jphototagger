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
package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.ModifyImageCollections;
import de.elmar_baumann.imv.view.dialogs.ImageCollectionsDialog;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-00-10
 */
public final class ControllerAddToImageCollection implements ActionListener {

    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel thumbnailsPanel =
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
            ModifyImageCollections.addImagesToCollection(collectionName,
                    FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dialog = new ImageCollectionsDialog(
                GUI.INSTANCE.getAppFrame());
        dialog.setVisible(true);
        return dialog.getSelectedCollectionName();
    }
}
