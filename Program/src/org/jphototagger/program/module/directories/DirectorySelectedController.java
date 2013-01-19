package org.jphototagger.program.module.directories;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Listens for selections of items in the directory tree view. A tree item represents a directory. If a new item is
 * selected, this controller sets the files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class DirectorySelectedController implements TreeSelectionListener {

    private static final String KEY_RECURSIVE = "DirectorySelectedController.DirectoriesRecursive";

    public DirectorySelectedController() {
        restoreRecursive();
        listen();
    }

    private void listen() {
        GUI.getDirectoriesTree().addTreeSelectionListener(this);
        GUI.getCheckBoxDirectoriesRecursive().addActionListener(recursiveListener);
        AnnotationProcessor.process(this);
    }

    private final ActionListener recursiveListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean recursive = ((AbstractButton) e.getSource()).isSelected();
            persistRecursive(recursive);
            setFilesToThumbnailsPanel(null);
        }
    };

    private void persistRecursive(boolean recursive) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(KEY_RECURSIVE, recursive);
    }

    private void restoreRecursive() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        GUI.getCheckBoxDirectoriesRecursive().setSelected(prefs.getBoolean(KEY_RECURSIVE));
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath() && !DirectoriesPopupMenu.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel(null);
        }
    }

    private static boolean isMyOrigin(OriginOfDisplayedThumbnails origin) {
        return origin == OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY
                || origin == OriginOfDisplayedThumbnails.FILES_IN_DIRECTORY_RECURSIVE;
    }

    private static OriginOfDisplayedThumbnails getMyOrigin() {
        return GUI.getCheckBoxDirectoriesRecursive().isSelected()
                ? OriginOfDisplayedThumbnails.FILES_IN_DIRECTORY_RECURSIVE
                : OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY;
    }

    private static boolean isRecursive() {
        return getMyOrigin() == OriginOfDisplayedThumbnails.FILES_IN_DIRECTORY_RECURSIVE;
    }

    private void setFilesToThumbnailsPanel(ThumbnailsPanelSettings settings) {
        EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(settings));
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        OriginOfDisplayedThumbnails origin = evt.getTypeOfDisplayedImages();
        if (isMyOrigin(origin)) {
            setFilesToThumbnailsPanel(evt.getThumbnailsPanelSettings());
        }
    }

    private class ShowThumbnails implements Runnable {

        private final ThumbnailsPanelSettings panelSettings;

        ShowThumbnails(ThumbnailsPanelSettings panelSettings) {
            this.panelSettings = panelSettings;
        }

        @Override
        public void run() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    showThumbnails();
                }
            });
        }

        private void showThumbnails() {
            if (GUI.getDirectoriesTree().getSelectionCount() > 0) {
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                File selectedDirectory = new File(getDirectorynameFromTree());
                OriginOfDisplayedThumbnails origin = getMyOrigin();
                setTitle(selectedDirectory);
                GUI.getThumbnailsPanel().setFiles(getFiles(selectedDirectory), origin);
                GUI.getThumbnailsPanel().applyThumbnailsPanelSettings(panelSettings);
                waitDisplayer.hide();
            }
        }

        private List<File> getFiles(File directory) {
            return isRecursive()
                    ? FileFilterUtil.getImageFilesOfDirAndSubDirs(directory)
                    : FileFilterUtil.getImageFilesOfDirectory(directory);
        }

        private void setTitle(File selectedDirectory) {
            String title = Bundle.getString(ShowThumbnails.class, "ControllerDirectorySelected.AppFrame.Title.Directory", selectedDirectory);
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
        }

        private String getDirectorynameFromTree() {
            TreePath treePath = GUI.getDirectoriesTree().getSelectionPath();
            if (treePath.getLastPathComponent() instanceof File) {
                return ((File) treePath.getLastPathComponent()).getAbsolutePath();
            } else {
                return treePath.getLastPathComponent().toString();
            }
        }
    }
}
