/*
 * @(#)FavoritesHelper.java    Created on 2010-01-20
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

package org.jphototagger.program.helper;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.database.DatabaseFavorites;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.ImageFilteredDirectory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.dialogs.FavoritePropertiesDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel.Settings;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class FavoritesHelper {
    private FavoritesHelper() {}

    public static void updateFavorite(final Favorite favorite) {
        FavoritePropertiesDialog dialog = new FavoritePropertiesDialog();

        dialog.setFavoriteName(favorite.getName());
        dialog.setDirectory(favorite.getDirectory());
        dialog.setVisible(true);

        if (dialog.accepted()) {
            final String  newFavName = dialog.getFavoriteName();
            final String  oldFavName = favorite.getName();
            final File    dir        = dialog.getDirectory();
            final boolean renamed    = !newFavName.equals(oldFavName);
            final int     index      = favorite.getIndex();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DatabaseFavorites db = DatabaseFavorites.INSTANCE;

                    if (renamed) {
                        if (db.updateRename(oldFavName, newFavName)) {
                            favorite.setName(newFavName);
                        } else {
                            MessageDisplayer.error(
                                null, "FavoritesHelper.Error.Rename",
                                oldFavName);

                            return;
                        }
                    }

                    Favorite newFavorite = new Favorite(newFavName, dir, index);

                    if (!db.update(newFavorite)) {
                        MessageDisplayer.error(null,
                                               "FavoritesHelper.Error.Update",
                                               newFavName);
                    }
                }
            });
        }
    }

    public static void deleteFavorite(final Favorite favorite) {
        if (confirmDelete(favorite.getName())) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ModelFactory.INSTANCE.getModel(
                        TreeModelFavorites.class).delete(favorite);
                }
            });
        }
    }

    private static boolean confirmDelete(String favoriteName) {
        return MessageDisplayer.confirmYesNo(null,
                "FavoritesHelper.Confirm.Delete", favoriteName);
    }

    /**
     * Returns the selected node of the favorites tree.
     *
     * @return node or null if no node is selected
     */
    public static DefaultMutableTreeNode getSelectedNode() {
        TreePath path =
            GUI.INSTANCE.getAppPanel().getTreeFavorites().getSelectionPath();

        if (path != null) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        }

        return null;
    }

    /**
     * Returns the directory of the selected node of the favorites tree.
     *
     * @return directory or null if no node is selected
     */
    public static File getSelectedDir() {
        DefaultMutableTreeNode selNode    = getSelectedNode();
        Object                 userObject = selNode.getUserObject();

        if (userObject instanceof Favorite) {
            Favorite favoriteDirectory = (Favorite) userObject;

            return favoriteDirectory.getDirectory();
        } else if (userObject instanceof File) {
            return (File) userObject;
        }

        return null;
    }

    /**
     * Returns the files in the directory of the selected node of the favorites
     * tree.
     *
     * @return files or empty list
     */
    public static List<File> getFilesOfSelectedtDirectory() {
        TreePath path =
            GUI.INSTANCE.getAppPanel().getTreeFavorites().getSelectionPath();

        if (path != null) {
            File                   dir  = null;
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();

            if (userObject instanceof Favorite) {
                Favorite favoriteDirectory = (Favorite) userObject;

                dir = favoriteDirectory.getDirectory();
            } else if (userObject instanceof File) {
                dir = (File) userObject;
            }

            if (dir != null) {
                return ImageFilteredDirectory.getImageFilesOfDirectory(dir);
            }
        }

        return new ArrayList<File>();
    }

    public static void setFilesToThumbnailPanel(List<File> files,
            ThumbnailsPanel.Settings settings) {
        SwingUtilities.invokeLater(new SetFiles(files, settings));
    }

    private static class SetFiles implements Runnable {
        private final AppPanel        appPanel        =
            GUI.INSTANCE.getAppPanel();
        private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
        private final EditMetadataPanels editPanels =
            appPanel.getEditMetadataPanels();
        private final List<File>               files;
        private final ThumbnailsPanel.Settings tnPanelSettings;

        public SetFiles(List<File> files, Settings settings) {
            this.files           = files;    // No copy due performance
            this.tnPanelSettings = settings;
        }

        @Override
        public void run() {
            ControllerSortThumbnails.setLastSort();
            setTitle();
            thumbnailsPanel.setFiles(files, Content.FAVORITE);
            thumbnailsPanel.apply(tnPanelSettings);
            setMetadataEditable();
        }

        private void setTitle() {
            File dir = FavoritesHelper.getSelectedDir();

            GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "FavoritesHelper.AppFrame.Title.FavoriteDirectory",
                    (dir == null)
                    ? "?"
                    : dir));
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }
}
