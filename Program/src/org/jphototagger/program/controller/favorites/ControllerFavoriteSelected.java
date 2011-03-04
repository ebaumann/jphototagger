package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.helper.FavoritesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerFavoriteSelected implements TreeSelectionListener, RefreshListener {
    public ControllerFavoriteSelected() {
        listen();
    }

    private void listen() {
        GUI.getFavoritesTree().getSelectionModel().addTreeSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.FAVORITE);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            FavoritesHelper.setFilesToThumbnailPanel(FavoritesHelper.getFilesOfSelectedtDirectory(), null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getFavoritesTree().getSelectionCount() > 0) {
            FavoritesHelper.setFilesToThumbnailPanel(FavoritesHelper.getFilesOfSelectedtDirectory(), evt.getSettings());
        }
    }
}
