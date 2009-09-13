/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import java.io.File;
import java.util.List;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Listens for selections of items in the directory tree view. A tree item
 * represents a directory. If a new item is selected, this controller sets the
 * files of the selected directory to the image file thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerDirectorySelected implements TreeSelectionListener,
                                                          RefreshListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final EditMetadataPanelsArray editPanels =
            appPanel.getEditPanelsArray();
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
        if (e.isAddedPath() &&
                !PopupMenuDirectories.INSTANCE.isTreeSelected()) {
            setFilesToThumbnailsPanel();
        }
    }

    @Override
    public void refresh() {
        setFilesToThumbnailsPanel();
    }

    private void setFilesToThumbnailsPanel() {
        SwingUtilities.invokeLater(new ShowThumbnails());
    }

    private class ShowThumbnails implements Runnable {

        @Override
        public void run() {
            if (treeDirectories.getSelectionCount() > 0) {
                File selectedDirectory =
                        new File(getDirectorynameFromTree());
                imageFilteredDirectory.setDirectory(selectedDirectory);
                List<File> files = ImageFilteredDirectory.
                        getImageFilesOfDirectory(selectedDirectory);
                thumbnailsPanel.setFiles(files,
                        Content.DIRECTORY);
                setMetadataEditable();
            }
        }

        private String getDirectorynameFromTree() {
            TreePath treePath = treeDirectories.getSelectionPath();
            if (treePath.getLastPathComponent() instanceof File) {
                return ((File) treePath.getLastPathComponent()).getAbsolutePath();
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
