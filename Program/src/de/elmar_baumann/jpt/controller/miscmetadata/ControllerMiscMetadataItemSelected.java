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
package de.elmar_baumann.jpt.controller.miscmetadata;

import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class ControllerMiscMetadataItemSelected implements
        TreeSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeMiscMetadata();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerMiscMetadataItemSelected() {
        listen();
    }

    private void listen() {
        tree.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.MISC_METADATA);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            SwingUtilities.invokeLater(new ShowThumbnails(e.
                    getNewLeadSelectionPath()));
        }
    }

    @Override
    public void refresh() {
        if (tree.getSelectionCount() == 1) {
            SwingUtilities.invokeLater(new ShowThumbnails(
                    tree.getSelectionPath()));
        }
    }

    private class ShowThumbnails implements Runnable {

        private final TreePath treePath;

        public ShowThumbnails(TreePath treePath) {
            this.treePath = treePath;
        }

        @Override
        public void run() {
            Object lastPathComponent = treePath.getLastPathComponent();
            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
        }

        private void setFilesOfPossibleNodeToThumbnailsPanel(
                Object lastPathComponent) {
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) lastPathComponent;
                setFilesOfNodeToThumbnailsPanel(node);
            }
        }

        private void setFilesOfNodeToThumbnailsPanel(DefaultMutableTreeNode node) {
            if (node.isLeaf()) {
                Object userObject = node.getUserObject();
                Object parentUserObject =
                        ((DefaultMutableTreeNode) node.getParent()).
                        getUserObject();
                if (parentUserObject instanceof Column) {
                    Column column = (Column) parentUserObject;
                    thumbnailsPanel.setFiles(db.getFilesJoinTable(column,
                            userObject.toString()),
                            Content.MISC_METADATA);
                }
            } else {
                thumbnailsPanel.setFiles(new ArrayList<File>(),
                        Content.MISC_METADATA);
            }
        }
    }
}
