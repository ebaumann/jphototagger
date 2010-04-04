/*
 * @(#)ControllerFavoriteSelected.java    Created on 2008-09-24
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

package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.helper.FavoritesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTree;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann
 */
public final class ControllerFavoriteSelected
        implements TreeSelectionListener, RefreshListener {
    private final AppPanel        appPanel                =
        GUI.INSTANCE.getAppPanel();
    private final JTree           treeFavoriteDirectories =
        appPanel.getTreeFavorites();
    private final ThumbnailsPanel thumbnailsPanel         =
        appPanel.getPanelThumbnails();

    public ControllerFavoriteSelected() {
        listen();
    }

    private void listen() {
        treeFavoriteDirectories.getSelectionModel().addTreeSelectionListener(
            this);
        thumbnailsPanel.addRefreshListener(this, Content.FAVORITE);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            FavoritesHelper.setFilesToThumbnailPanel(
                FavoritesHelper.getFilesOfSelectedtDirectory(), null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (treeFavoriteDirectories.getSelectionCount() > 0) {
            FavoritesHelper.setFilesToThumbnailPanel(
                FavoritesHelper.getFilesOfSelectedtDirectory(),
                evt.getSettings());
        }
    }
}
