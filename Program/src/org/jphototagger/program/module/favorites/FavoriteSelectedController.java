package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Listens for selections of items in the favorite directories tree view. A tree
 * item represents a directory. If a new item is selected, this controller sets
 * the files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class FavoriteSelectedController implements TreeSelectionListener {

    private static final String KEY_RECURSIVE = "FavoriteSelectedController.DirectoriesRecursive";

    public FavoriteSelectedController() {
        restoreRecursive();
        listen();
    }

    private void listen() {
        GUI.getCheckBoxFavoritesRecursive().addActionListener(recursiveListener);
        GUI.getFavoritesTree().getSelectionModel().addTreeSelectionListener(this);
        AnnotationProcessor.process(this);
    }

    private final ActionListener recursiveListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean recursive = ((AbstractButton) e.getSource()).isSelected();
            persistRecursive(recursive);
            setFiles(null);
        }
    };

    private void persistRecursive(boolean recursive) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(KEY_RECURSIVE, recursive);
    }

    private void restoreRecursive() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        GUI.getCheckBoxFavoritesRecursive().setSelected(prefs.getBoolean(KEY_RECURSIVE));
    }

    private static boolean isMyOrigin(OriginOfDisplayedThumbnails origin) {
        return origin == OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY
                || origin == OriginOfDisplayedThumbnails.FILES_IN_FAVORITE_DIRECTORY_RECURSIVE;
    }

    private static OriginOfDisplayedThumbnails getMyOrigin() {
        return GUI.getCheckBoxFavoritesRecursive().isSelected()
                ? OriginOfDisplayedThumbnails.FILES_IN_FAVORITE_DIRECTORY_RECURSIVE
                : OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY;
    }

    private static boolean isRecursive() {
        return getMyOrigin() == OriginOfDisplayedThumbnails.FILES_IN_FAVORITE_DIRECTORY_RECURSIVE;
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            setFiles(null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getFavoritesTree().getSelectionCount() > 0) {
            OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
            if (isMyOrigin(origin)) {
                setFiles(evt.getThumbnailsPanelSettings());
            }
        }
    }

    private void setFiles(ThumbnailsPanelSettings settings) {
        List<File> files = getFiles(FavoritesUtil.getSelectedDir());
        FavoritesUtil.setFilesToThumbnailPanel(files, settings, getMyOrigin());
    }

    private List<File> getFiles(File directory) {
        return directory == null
                ? Collections.<File>emptyList()
                : isRecursive()
                ? FileFilterUtil.getImageFilesOfDirAndSubDirs(directory)
                : FileFilterUtil.getImageFilesOfDirectory(directory);
    }
}
