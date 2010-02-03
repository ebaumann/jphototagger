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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.FavoritePropertiesDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-20
 */
public final class FavoritesHelper {

    public static void updateFavorite(final Favorite favorite) {
        FavoritePropertiesDialog dialog = new FavoritePropertiesDialog();

        dialog.setFavoriteName(favorite.getName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            final String favoriteName  = dialog.getFavoriteName();
            final String directoryName = dialog.getDirectoryName();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreeModelFavorites model = ModelFactory.INSTANCE.getModel(TreeModelFavorites.class);
                    model.update(favorite, new Favorite(favoriteName,
                                                        directoryName,
                                                        favorite.getIndex()));
                }
            });
        }
    }

    public static void deleteFavorite(final Favorite favoirte) {
        if (confirmDelete(favoirte.getName())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ModelFactory.INSTANCE.getModel(TreeModelFavorites.class).delete(favoirte);
                }
            });
        }
    }

    private static boolean confirmDelete(String favoriteName) {
        return MessageDisplayer.confirmYesNo(null, "FavoritesHelper.Confirm.Delete", favoriteName);
    }

    public static List<File> getFilesOfCurrentDirectory() {
        TreePath path = GUI.INSTANCE.getAppPanel().getTreeFavorites().getSelectionPath();
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
                return ImageFilteredDirectory.getImageFilesOfDirectory(dir);
            }
        }
        return new ArrayList<File>();
    }

    public static void setFilesToThumbnailPanel(List<File> files, ThumbnailsPanel.Settings settings) {
        SwingUtilities.invokeLater(new SetFiles(files, settings));
    }

    private static class SetFiles implements Runnable {

        private final AppPanel                 appPanel         = GUI.INSTANCE.getAppPanel();
        private final ThumbnailsPanel          thumbnailsPanel  = appPanel.getPanelThumbnails();
        private final EditMetadataPanels       editPanels       = appPanel.getEditMetadataPanels();
        private final ThumbnailsPanel.Settings tnPanelSettings;
        private final List<File>               files;

        public SetFiles(List<File> files, Settings settings) {
            this.files           = files; // No copy due performance
            this.tnPanelSettings = settings;
        }

        @Override
        public void run() {
            ControllerSortThumbnails.setLastSort();
            setTitle(files);
            thumbnailsPanel.setFiles(files, Content.FAVORITE);
            thumbnailsPanel.apply(tnPanelSettings);
            setMetadataEditable();
        }

        private void setTitle(List<File> files) {
            if (files.isEmpty()) return;

            File dir = files.get(0).getParentFile();
            GUI.INSTANCE.getAppFrame().setTitle(
                    Bundle.getString("FavoritesHelper.AppFrame.Title.FavoriteDirectory", dir.getName()));
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }

    private FavoritesHelper() {
    }

}
