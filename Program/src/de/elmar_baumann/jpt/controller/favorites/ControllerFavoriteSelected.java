/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.controller.favorites;

import de.elmar_baumann.jpt.event.RefreshEvent;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.helper.FavoritesHelper;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class ControllerFavoriteSelected implements TreeSelectionListener, RefreshListener {

    private final AppPanel        appPanel                = GUI.INSTANCE.getAppPanel();
    private final JTree           treeFavoriteDirectories = appPanel.getTreeFavorites();
    private final ThumbnailsPanel thumbnailsPanel         = appPanel.getPanelThumbnails();

    public ControllerFavoriteSelected() {
        listen();
    }

    private void listen() {
        treeFavoriteDirectories.getSelectionModel().addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FAVORITE);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            FavoritesHelper.setFilesToThumbnailPanel(
                    FavoritesHelper.getFilesOfSelectedtDirectory(), null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (treeFavoriteDirectories.getSelectionCount() > 0) {
            FavoritesHelper.setFilesToThumbnailPanel(
                    FavoritesHelper.getFilesOfSelectedtDirectory(), evt.getSettings());
        }
    }
}
