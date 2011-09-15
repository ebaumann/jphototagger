package org.jphototagger.program.controller.favorites;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.program.helper.FavoritesHelper;
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
            FavoritesHelper.setFilesToThumbnailPanel(FavoritesHelper.getFilesOfSelectedtDirectory(), null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getFavoritesTree().getSelectionCount() > 0) {
            TypeOfDisplayedImages typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (TypeOfDisplayedImages.FAVORITE.equals(typeOfDisplayedImages)) {
                FavoritesHelper.setFilesToThumbnailPanel(FavoritesHelper.getFilesOfSelectedtDirectory(), evt.getThumbnailsPanelSettings());
            }
        }
    }
}
