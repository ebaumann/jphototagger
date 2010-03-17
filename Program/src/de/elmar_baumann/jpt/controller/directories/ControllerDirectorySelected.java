/*
 * @(#)ControllerDirectorySelected.java    2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.event.RefreshEvent;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;

import java.io.File;

import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 * Listens for selections of items in the directory tree view. A tree item
 * represents a directory. If a new item is selected, this controller sets the
 * files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDirectorySelected
        implements TreeSelectionListener, RefreshListener {
    private final AppPanel           appPanel        =
        GUI.INSTANCE.getAppPanel();
    private final JTree              treeDirectories =
        appPanel.getTreeDirectories();
    private final EditMetadataPanels editPanels      =
        appPanel.getEditMetadataPanels();
    private final ThumbnailsPanel thumbnailsPanel =
        appPanel.getPanelThumbnails();
    private final ImageFilteredDirectory imageFilteredDirectory =
        new ImageFilteredDirectory();

    public ControllerDirectorySelected() {
        listen();
    }

    private void listen() {
        treeDirectories.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.DIRECTORY);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath() &&!PopupMenuDirectories.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        setFilesToThumbnailsPanel(evt.getSettings());
    }

    private void setFilesToThumbnailsPanel(ThumbnailsPanel.Settings settings) {
        SwingUtilities.invokeLater(new ShowThumbnails(settings));
    }

    private class ShowThumbnails implements Runnable {
        private final ThumbnailsPanel.Settings panelSettings;

        public ShowThumbnails(ThumbnailsPanel.Settings settings) {
            panelSettings = settings;
        }

        @Override
        public void run() {
            if (treeDirectories.getSelectionCount() > 0) {
                File selectedDirectory = new File(getDirectorynameFromTree());

                imageFilteredDirectory.setDirectory(selectedDirectory);

                List<File> files =
                    ImageFilteredDirectory.getImageFilesOfDirectory(
                        selectedDirectory);

                setTitle(selectedDirectory);
                ControllerSortThumbnails.setLastSort();
                thumbnailsPanel.setFiles(files, Content.DIRECTORY);
                thumbnailsPanel.apply(panelSettings);
                setMetadataEditable();
            }
        }

        private void setTitle(File selectedDirectory) {
            GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerDirectorySelected.AppFrame.Title.Directory",
                    selectedDirectory));
        }

        private String getDirectorynameFromTree() {
            TreePath treePath = treeDirectories.getSelectionPath();

            if (treePath.getLastPathComponent() instanceof File) {
                return ((File) treePath.getLastPathComponent())
                    .getAbsolutePath();
            } else {
                return treePath.getLastPathComponent().toString();
            }
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }
}
