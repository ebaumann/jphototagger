package org.jphototagger.program.module.directories;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.concurrent.DefaultCancelRequest;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.tasks.ReplaceableThread;
import org.openide.util.Lookup;

/**
 * Listens for selections of items in the directory tree view. A tree item represents a directory. If a new item is
 * selected, this controller sets the files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class DirectorySelectedController implements TreeSelectionListener {

    private static final String KEY_RECURSIVE = "DirectorySelectedController.DirectoriesRecursive";
    private static final ReplaceableThread SCHEDULER = new ReplaceableThread();

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
        return origin.isFilesInSameDirectory() || origin.isFilesInDirectoryRecursive();
    }

    private static OriginOfDisplayedThumbnails getMyOrigin() {
        return GUI.getCheckBoxDirectoriesRecursive().isSelected()
                ? OriginOfDisplayedThumbnails.FILES_IN_DIRECTORY_RECURSIVE
                : OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY;
    }

    private static boolean isRecursive() {
        return getMyOrigin().isFilesInDirectoryRecursive();
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
        if (isMyOrigin(origin)) {
            setFilesToThumbnailsPanel(evt.getThumbnailsPanelSettings());
        }
    }

    private void setFilesToThumbnailsPanel(ThumbnailsPanelSettings settings) {
        if (GUI.getDirectoriesTree().getSelectionCount() > 0) {
            File directory = getSelectedDirectoryFromTree();
            OriginOfDisplayedThumbnails origin = getMyOrigin();
            SCHEDULER.setTask(new FileReader(directory, isRecursive(), settings, origin));
        }
    }

    private File getSelectedDirectoryFromTree() {
        TreePath treePath = GUI.getDirectoriesTree().getSelectionPath();
        if (treePath.getLastPathComponent() instanceof File) {
            return ((File) treePath.getLastPathComponent());
        } else {
            return new File(treePath.getLastPathComponent().toString());
        }
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
            this.message = Bundle.getString(FileReader.class, "FileReader.ProgressStarted", StringUtil.toMaxLengthEndingDots(directory.getName(), 60));
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
                    GUI.getThumbnailsPanel().setFiles(files, origin);
                    GUI.getThumbnailsPanel().applyThumbnailsPanelSettings(settings);
                    setTitle(directory);
                }
            });
        }

        private void setTitle(File selectedDirectory) {
            String title = Bundle.getString(FileReader.class, "FileReader.AppFrame.Title.Directory", selectedDirectory);
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
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
