/*
 * @(#)ControllerDirectorySelected.java    Created on 2008-10-05
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

package org.jphototagger.program.controller.directories;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.io.ImageFilteredDirectory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

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
    private final AppPanel           appPanel   = GUI.INSTANCE.getAppPanel();
    private final JTree              tree       = appPanel.getTreeDirectories();
    private final EditMetadataPanels editPanels =
        appPanel.getEditMetadataPanels();
    private final ThumbnailsPanel        tnPanel                =
        appPanel.getPanelThumbnails();
    private final ImageFilteredDirectory imageFilteredDirectory =
        new ImageFilteredDirectory();

    public ControllerDirectorySelected() {
        listen();
    }

    private void listen() {
        tree.addTreeSelectionListener(this);
        tnPanel.addRefreshListener(this, Content.DIRECTORY);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()
                &&!PopupMenuDirectories.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

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
            if (tree.getSelectionCount() > 0) {
                File selectedDirectory = new File(getDirectorynameFromTree());

                imageFilteredDirectory.setDirectory(selectedDirectory);

                List<File> files =
                    ImageFilteredDirectory.getImageFilesOfDirectory(
                        selectedDirectory);

                setTitle(selectedDirectory);
                ControllerSortThumbnails.setLastSort();
                tnPanel.setFiles(files, Content.DIRECTORY);
                tnPanel.apply(panelSettings);
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
            TreePath treePath = tree.getSelectionPath();

            if (treePath.getLastPathComponent() instanceof File) {
                return ((File) treePath.getLastPathComponent())
                    .getAbsolutePath();
            } else {
                return treePath.getLastPathComponent().toString();
            }
        }

        private void setMetadataEditable() {
            if (!tnPanel.isFileSelected()) {
                editPanels.setEditable(false);
            }
        }
    }
}
