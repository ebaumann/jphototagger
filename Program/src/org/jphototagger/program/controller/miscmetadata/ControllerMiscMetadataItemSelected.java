package org.jphototagger.program.controller.miscmetadata;

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerMiscMetadataItemSelected implements TreeSelectionListener {

    public ControllerMiscMetadataItemSelected() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getMiscMetadataTree().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(evt.getNewLeadSelectionPath(), null));
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getMiscMetadataTree().getSelectionCount() == 1) {
            TypeOfDisplayedImages typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (TypeOfDisplayedImages.MISC_METADATA.equals(typeOfDisplayedImages)) {
                EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(GUI.getMiscMetadataTree().getSelectionPath(), evt.getThumbnailsPanelSettings()));
            }
        }
    }

    private class ShowThumbnails implements Runnable {

        private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);
        private final ThumbnailsPanelSettings tnPanelSettings;
        private final TreePath treePath;

        ShowThumbnails(TreePath treePath, ThumbnailsPanelSettings settings) {
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

                if (parentUserObject instanceof MetaDataValue) {
                    MetaDataValue mdValue = (MetaDataValue) parentUserObject;

                    setTitle(mdValue, userObject);
                    ControllerSortThumbnails.setLastSort();
                    tnPanel.setFiles(repo.getImageFilesWhereMetaDataValueHasExactValue(mdValue,
                            userObject.toString()), TypeOfDisplayedImages.MISC_METADATA);
                    tnPanel.apply(tnPanelSettings);
                } else {
                    setTitle();
                }
            } else if (userObject instanceof MetaDataValue) {
                MetaDataValue mdValue = (MetaDataValue) userObject;

                setTitle(mdValue);
                ControllerSortThumbnails.setLastSort();
                tnPanel.setFiles(repo.getImageFilesContainingAVauleInMetaDataValue(mdValue), TypeOfDisplayedImages.MISC_METADATA);
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

        private void setTitle(MetaDataValue mdValue) {
            GUI.getAppFrame().setTitle(Bundle.getString(ShowThumbnails.class, "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Value", mdValue.getDescription()));
        }

        private void setTitle(MetaDataValue mdValue, Object userObject) {
            GUI.getAppFrame().setTitle(Bundle.getString(ShowThumbnails.class, "ControllerMiscMetadataItemSelected.AppFrame.Title.Metadata.Object", mdValue.getDescription(), userObject.toString()));
        }
    }
}
