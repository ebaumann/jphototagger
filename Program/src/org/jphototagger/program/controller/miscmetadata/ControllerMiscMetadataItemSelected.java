/*
 * @(#)ControllerMiscMetadataItemSelected.java    Created on 2009-06-12
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

package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.ArrayList;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann
 */
public final class ControllerMiscMetadataItemSelected
        implements TreeSelectionListener, RefreshListener {
    private final AppPanel        appPanel        = GUI.INSTANCE.getAppPanel();
    private final JTree           tree            =
        appPanel.getTreeMiscMetadata();
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
            SwingUtilities.invokeLater(
                new ShowThumbnails(e.getNewLeadSelectionPath(), null));
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (tree.getSelectionCount() == 1) {
            SwingUtilities.invokeLater(
                new ShowThumbnails(tree.getSelectionPath(), evt.getSettings()));
        }
    }

    private class ShowThumbnails implements Runnable {
        private final ThumbnailsPanel.Settings tnPanelSettings;
        private final TreePath                 treePath;

        public ShowThumbnails(TreePath treePath,
                              ThumbnailsPanel.Settings settings) {
            if (treePath == null) {
                throw new NullPointerException("treePath == null");
            }

            this.treePath   = treePath;
            tnPanelSettings = settings;
        }

        @Override
        public void run() {
            Object lastPathComponent = treePath.getLastPathComponent();

            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
        }

        private void setFilesOfPossibleNodeToThumbnailsPanel(
                Object lastPathComponent) {
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                setFilesOfNodeToThumbnailsPanel(
                    (DefaultMutableTreeNode) lastPathComponent);
            }
        }

        private void setFilesOfNodeToThumbnailsPanel(
                DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();

            if (node.isLeaf()) {
                Object parentUserObject =
                    ((DefaultMutableTreeNode) node.getParent()).getUserObject();

                if (parentUserObject instanceof Column) {
                    Column column = (Column) parentUserObject;

                    setTitle(column, userObject);
                    ControllerSortThumbnails.setLastSort();
                    thumbnailsPanel
                        .setFiles(DatabaseImageFiles.INSTANCE
                            .getImageFilesWithColumnContent(column,
                                userObject.toString()), Content.MISC_METADATA);
                    thumbnailsPanel.apply(tnPanelSettings);
                } else {
                    setTitle();
                }
            } else if (userObject instanceof Column) {
                Column column = (Column) userObject;

                setTitle(column);
                ControllerSortThumbnails.setLastSort();
                thumbnailsPanel.setFiles(
                    DatabaseImageFiles.INSTANCE.getFilesNotNullIn(column),
                    Content.MISC_METADATA);
                thumbnailsPanel.apply(tnPanelSettings);
            } else {
                ControllerSortThumbnails.setLastSort();
                thumbnailsPanel.setFiles(new ArrayList<File>(),
                                         Content.MISC_METADATA);
                thumbnailsPanel.apply(tnPanelSettings);
                setTitle();
            }
        }

        // 1 path where thumbnailsPanel.apply(tnPanelSettings) is not to call
        private void setTitle() {
            GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata"));
        }

        private void setTitle(Column column) {
            GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Column",
                    column.getDescription()));
        }

        private void setTitle(Column column, Object userObject) {
            GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Object",
                    column.getDescription() + " " + userObject.toString()));
        }
    }
}
