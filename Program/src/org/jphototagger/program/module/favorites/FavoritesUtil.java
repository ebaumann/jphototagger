package org.jphototagger.program.module.favorites;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.repository.FavoritesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FavoritesUtil {

    private FavoritesUtil() {
    }

    public static void updateFavorite(final Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        FavoritePropertiesDialog dlg = new FavoritePropertiesDialog();

        dlg.setFavoriteName(favorite.getName());
        dlg.setDirectory(favorite.getDirectory());
        dlg.setVisible(true);

        if (dlg.isAccepted() && !dlg.isEqualsTo(favorite)) {
            File oldDir = favorite.getDirectory();

            favorite.setName(dlg.getFavoriteName());
            favorite.setDirectory(dlg.getDirectory());

            final boolean dirChanged = !favorite.getDirectory().equals(oldDir);

            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    FavoritesRepository repo = Lookup.getDefault().lookup(FavoritesRepository.class);

                    if (repo.updateFavorite(favorite)) {
                        if (dirChanged) {
                            GUI.refreshThumbnailsPanel();
                        }
                    } else {
                        String message = Bundle.getString(FavoritesUtil.class, "FavoritesHelper.Error.Update", favorite);
                        MessageDisplayer.error(null, message);
                    }
                }
            });
        }
    }

    public static void deleteFavorite(final Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        if (confirmDelete(favorite.getName())) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class).delete(favorite);
                }
            });
        }
    }

    private static boolean confirmDelete(String favoriteName) {
        String message = Bundle.getString(FavoritesUtil.class, "FavoritesHelper.Confirm.Delete", favoriteName);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    /**
     * Returns the selected node of the favorites tree.
     *
     * @return node or null if no node is selected
     */
    public static DefaultMutableTreeNode getSelectedNode() {
        TreePath path = GUI.getAppPanel().getTreeFavorites().getSelectionPath();

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
        DefaultMutableTreeNode selNode = getSelectedNode();
        Object userObject = selNode.getUserObject();

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
        TreePath path = GUI.getAppPanel().getTreeFavorites().getSelectionPath();

        if (path != null) {
            File dir = null;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();

            if (userObject instanceof Favorite) {
                Favorite favoriteDirectory = (Favorite) userObject;

                dir = favoriteDirectory.getDirectory();
            } else if (userObject instanceof File) {
                dir = (File) userObject;
            }

            if (dir != null) {
                return FileFilterUtil.getImageFilesOfDirectory(dir);
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns the selected directory in the tree with favorite directories.
     *
     * @return directory or null if no directory is selected
     */
    public static File getSelectedFavorite() {
        JTree tree = GUI.getAppPanel().getTreeFavorites();
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
            Object userObject = node.getUserObject();

            if (userObject instanceof Favorite) {
                Favorite favorite = (Favorite) userObject;

                return favorite.getDirectory();
            } else if (userObject instanceof File) {
                return (File) userObject;
            }
        }

        return null;
    }

    /**
     *
     * @param files
     * @param settings can be null
     * @param origin
     */
    public static void setFilesToThumbnailPanel(List<File> files, ThumbnailsPanelSettings settings, OriginOfDisplayedThumbnails origin) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        EventQueueUtil.invokeInDispatchThread(new SetFiles(files, settings, origin));
    }

    private static class SetFiles implements Runnable {

        private final ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        private final List<File> files;
        private final ThumbnailsPanelSettings tnPanelSettings;
        private final OriginOfDisplayedThumbnails origin;

        private SetFiles(List<File> files, ThumbnailsPanelSettings settings, OriginOfDisplayedThumbnails origin) {
            this.files = files;    // No copy due performance
            this.tnPanelSettings = settings;
            this.origin = origin;
        }

        @Override
        public void run() {
            setTitle();
            thumbnailsDisplayer.displayFiles(files, origin);
            thumbnailsDisplayer.applyThumbnailsPanelSettings(tnPanelSettings);
        }

        private void setTitle() {
            File dir = FavoritesUtil.getSelectedDir();
            Object dirString = dir == null ? "?" : dir;
            String title = Bundle.getString(SetFiles.class, "FavoritesHelper.AppFrame.Title.FavoriteDirectory", dirString);
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
        }
    }
}
