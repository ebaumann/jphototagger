package org.jphototagger.program.controller.miscmetadata;

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerMiscMetadataItemSelected implements TreeSelectionListener, RefreshListener {
    public ControllerMiscMetadataItemSelected() {
        listen();
    }

    private void listen() {
        GUI.getMiscMetadataTree().addTreeSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, TypeOfDisplayedImages.MISC_METADATA);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(evt.getNewLeadSelectionPath(), null));
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getMiscMetadataTree().getSelectionCount() == 1) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(GUI.getMiscMetadataTree().getSelectionPath(), evt.getSettings()));
        }
    }

    private class ShowThumbnails implements Runnable {
        private final ThumbnailsPanel.Settings tnPanelSettings;
        private final TreePath treePath;

        ShowThumbnails(TreePath treePath, ThumbnailsPanel.Settings settings) {
            if (treePath == null) {
                throw new NullPointerException("treePath == null");
            }

            this.treePath = treePath;
            tnPanelSettings = settings;
        }

        @Override
        public void run() {
            WaitDisplay.show();

            Object lastPathComponent = treePath.getLastPathComponent();

            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
            WaitDisplay.hide();
        }

        private void setFilesOfPossibleNodeToThumbnailsPanel(Object lastPathComponent) {
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                setFilesOfNodeToThumbnailsPanel((DefaultMutableTreeNode) lastPathComponent);
            }
        }

        private void setFilesOfNodeToThumbnailsPanel(DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            if (node.isLeaf()) {
                Object parentUserObject = ((DefaultMutableTreeNode) node.getParent()).getUserObject();

                if (parentUserObject instanceof Column) {
                    Column column = (Column) parentUserObject;

                    setTitle(column, userObject);
                    ControllerSortThumbnails.setLastSort();
                    tnPanel.setFiles(DatabaseImageFiles.INSTANCE.getImageFilesWhereColumnHasExactValue(column,
                            userObject.toString()), TypeOfDisplayedImages.MISC_METADATA);
                    tnPanel.apply(tnPanelSettings);
                } else {
                    setTitle();
                }
            } else if (userObject instanceof Column) {
                Column column = (Column) userObject;

                setTitle(column);
                ControllerSortThumbnails.setLastSort();
                tnPanel.setFiles(DatabaseImageFiles.INSTANCE.getImageFilesContainingAVauleInColumn(column), TypeOfDisplayedImages.MISC_METADATA);
                tnPanel.apply(tnPanelSettings);
            } else {
                ControllerSortThumbnails.setLastSort();
                tnPanel.setFiles(new ArrayList<File>(), TypeOfDisplayedImages.MISC_METADATA);
                tnPanel.apply(tnPanelSettings);
                setTitle();
            }
        }

        // 1 path where tnPanel.apply(tnPanelSettings) is not to call
        private void setTitle() {
            GUI.getAppFrame().setTitle(
                    Bundle.getString(ShowThumbnails.class, "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata"));
        }

        private void setTitle(Column column) {
            GUI.getAppFrame().setTitle(
                    Bundle.getString(ShowThumbnails.class, "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Column", column.getDescription()));
        }

        private void setTitle(Column column, Object userObject) {
            GUI.getAppFrame().setTitle(Bundle.getString(ShowThumbnails.class,
                    "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Object", column.getDescription(), userObject.toString()));
        }
    }
}
