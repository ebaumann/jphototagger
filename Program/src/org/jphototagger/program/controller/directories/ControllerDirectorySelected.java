package org.jphototagger.program.controller.directories;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Listens for selections of items in the directory tree view. A tree item
 * represents a directory. If a new item is selected, this controller sets the
 * files of the selected directory to the image file thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerDirectorySelected implements TreeSelectionListener, RefreshListener {
    public ControllerDirectorySelected() {
        listen();
    }

    private void listen() {
        GUI.getDirectoriesTree().addTreeSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.DIRECTORY);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath() &&!PopupMenuDirectories.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        setFilesToThumbnailsPanel(evt.getSettings());
    }

    private void setFilesToThumbnailsPanel(ThumbnailsPanel.Settings settings) {
        EventQueue.invokeLater(new ShowThumbnails(settings));
    }

    private class ShowThumbnails implements Runnable {
        private final ThumbnailsPanel.Settings panelSettings;

        ShowThumbnails(ThumbnailsPanel.Settings settings) {
            panelSettings = settings;
        }

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    showThumbnails();
                }
            });
        }

        private void showThumbnails() {
            if (GUI.getDirectoriesTree().getSelectionCount() > 0) {
                WaitDisplay.show();

                File selectedDirectory = new File(getDirectorynameFromTree());
                List<File> files = ImageFileFilterer.getImageFilesOfDirectory(selectedDirectory);

                setTitle(selectedDirectory);
                ControllerSortThumbnails.setLastSort();
                GUI.getThumbnailsPanel().setFiles(files, Content.DIRECTORY);
                GUI.getThumbnailsPanel().apply(panelSettings);
                setMetadataEditable();
                WaitDisplay.hide();
            }
        }

        private void setTitle(File selectedDirectory) {
            GUI.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerDirectorySelected.AppFrame.Title.Directory", selectedDirectory));
        }

        private String getDirectorynameFromTree() {
            TreePath treePath = GUI.getDirectoriesTree().getSelectionPath();

            if (treePath.getLastPathComponent() instanceof File) {
                return ((File) treePath.getLastPathComponent()).getAbsolutePath();
            } else {
                return treePath.getLastPathComponent().toString();
            }
        }

        private void setMetadataEditable() {
            if (!GUI.getThumbnailsPanel().isAFileSelected()) {
                GUI.getEditPanel().setEditable(false);
            }
        }
    }
}
