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
package de.elmar_baumann.jpt.controller.favorites;

import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.event.RefreshEvent;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class ControllerFavoriteSelected implements TreeSelectionListener, RefreshListener {

    private final AppPanel           appPanel                = GUI.INSTANCE.getAppPanel();
    private final JTree              treeFavoriteDirectories = appPanel.getTreeFavorites();
    private final ThumbnailsPanel    thumbnailsPanel         = appPanel.getPanelThumbnails();
    private final EditMetadataPanels editPanels              = appPanel.getEditMetadataPanels();

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
            update(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (treeFavoriteDirectories.getSelectionCount() > 0) {
            update(evt.getSettings());
        }
    }

    private void update(ThumbnailsPanel.Settings settings) {
        SwingUtilities.invokeLater(new SetFiles(settings));
    }

    private class SetFiles implements Runnable {

        private final ThumbnailsPanel.Settings tnPanelSettings;

        public SetFiles(Settings settings) {
            this.tnPanelSettings = settings;
        }

        @Override
        public void run() {
            List<File> files = getFilesOfCurrentDirectory();
            thumbnailsPanel.setFiles(files, Content.FAVORITE);
            thumbnailsPanel.apply(tnPanelSettings);
            setMetadataEditable();
        }

        private List<File> getFilesOfCurrentDirectory() {
            TreePath path = treeFavoriteDirectories.getSelectionPath();
            if (path != null) {
                File dir = null;
                DefaultMutableTreeNode node       = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object                 userObject = node.getUserObject();
                if (userObject instanceof Favorite) {
                    Favorite favoriteDirectory = (Favorite) userObject;
                    dir = favoriteDirectory.getDirectory();
                } else if (userObject instanceof File) {
                    dir = (File) userObject;
                }
                if (dir != null) {
                    setTitle(dir);
                    return ImageFilteredDirectory.getImageFilesOfDirectory(dir);
                }
            }
            return new ArrayList<File>();
        }

        private void setTitle(File dir) {
            GUI.INSTANCE.getAppFrame().setTitle(
                    Bundle.getString("AppFrame.Title.FavoriteDirectory", dir.getName()));
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }
}
