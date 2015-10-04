package org.jphototagger.program.module.favorites;

import java.awt.EventQueue;
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
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.DefaultCancelRequest;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.ReplaceableThread;
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
    private static final ReplaceableThread SCHEDULER = new ReplaceableThread();

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
        return origin.isFilesInSameFavoriteDirectory() || origin.isFilesInFavoriteDirectoryRecursive();
    }

    private static OriginOfDisplayedThumbnails getMyOrigin() {
        return GUI.getCheckBoxFavoritesRecursive().isSelected()
                ? OriginOfDisplayedThumbnails.FILES_IN_FAVORITE_DIRECTORY_RECURSIVE
                : OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY;
    }

    private static boolean isRecursive() {
        return getMyOrigin().isFilesInFavoriteDirectoryRecursive();
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
        File directory = FavoritesUtil.getSelectedDir();
        OriginOfDisplayedThumbnails origin = getMyOrigin();
        SCHEDULER.setTask(new FileReader(directory, isRecursive(), settings, origin));
    }

    private static final class FileReader implements Runnable, Cancelable {

        private final ThumbnailsPanelSettings settings;
        private final OriginOfDisplayedThumbnails origin;
        private final File directory;
        private final boolean recursive;
        private final DefaultCancelRequest cancelRequest = new DefaultCancelRequest();
        private final ProgressHandle progressHandle;
        private final String message;

        private FileReader(File directory, boolean recursive, ThumbnailsPanelSettings settings, OriginOfDisplayedThumbnails origin) {
            this.directory = directory;
            this.recursive = recursive;
            this.settings = settings;
            this.origin = origin;
            this.progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle();
            this.message = Bundle.getString(FileReader.class, "FileReader.ProgressStarted", directory.getName());
        }

        @Override
        public void run() {
            progressStarted();
            ThumbnailsDisplayer tnDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
            try {
                tnDisplayer.showMessagePopup(message, this);
                List<File> files = directory == null
                        ? Collections.<File>emptyList()
                        : recursive
                                ? FileFilterUtil.getImageFilesOfDirAndSubDirs(directory, cancelRequest)
                                : FileFilterUtil.getImageFilesOfDirectory(directory);
                if (!cancelRequest.isCancel()) {
                    setFilesToThumbnailPanel(files);
                }
            } finally {
                tnDisplayer.hideMessagePopup(this);
                progressHandle.progressEnded();
            }
        }

        private void setFilesToThumbnailPanel(final List<File> files) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FavoritesUtil.setFilesToThumbnailPanel(files, settings, origin);
                }
            });
        }

        private void progressStarted() {
            ProgressEvent evt = new ProgressEvent.Builder()
                    .source(this)
                    .indeterminate(true)
                    .stringPainted(true)
                    .stringToPaint(message)
                    .build();
            progressHandle.progressStarted(evt);
        }

        @Override
        public synchronized void cancel() {
            cancelRequest.setCancel(true);
        }
    }
}
