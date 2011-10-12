package org.jphototagger.program.module.favorites;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.program.resource.GUI;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class FavoriteSelectedController implements TreeSelectionListener {

    public FavoriteSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getFavoritesTree().getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            FavoritesUtil.setFilesToThumbnailPanel(FavoritesUtil.getFilesOfSelectedtDirectory(), null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getFavoritesTree().getSelectionCount() > 0) {
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY.equals(typeOfDisplayedImages)) {
                FavoritesUtil.setFilesToThumbnailPanel(FavoritesUtil.getFilesOfSelectedtDirectory(), evt.getThumbnailsPanelSettings());
            }
        }
    }
}
