package org.jphototagger.program.module.miscmetadata;

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class MiscMetadataItemSelectedController implements TreeSelectionListener {

    public MiscMetadataItemSelectedController() {
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
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA.equals(typeOfDisplayedImages)) {
                EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(GUI.getMiscMetadataTree().getSelectionPath(), evt.getThumbnailsPanelSettings()));
            }
        }
    }

    private class ShowThumbnails implements Runnable {

        private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
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
            WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
            waitDisplayer.show();
            Object lastPathComponent = treePath.getLastPathComponent();
            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
            waitDisplayer.hide();
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
                    tnPanel.setFiles(repo.findImageFilesWhereMetaDataValueHasExactValue(mdValue,
                            userObject.toString()), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
                    tnPanel.applyThumbnailsPanelSettings(tnPanelSettings);
                } else {
                    setTitle();
                }
            } else if (userObject instanceof MetaDataValue) {
                MetaDataValue mdValue = (MetaDataValue) userObject;

                setTitle(mdValue);
                tnPanel.setFiles(repo.findImageFilesContainingAVauleInMetaDataValue(mdValue), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
                tnPanel.applyThumbnailsPanelSettings(tnPanelSettings);
            } else {
                tnPanel.setFiles(new ArrayList<File>(), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
                tnPanel.applyThumbnailsPanelSettings(tnPanelSettings);
                setTitle();
            }
        }

        // 1 path where tnPanel.applyThumbnailsPanelSettings(tnPanelSettings) is not to call
        private void setTitle() {
            String title = Bundle.getString(ShowThumbnails.class, "MiscMetadataItemSelectedController.AppFrame.Title.Metadata");
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
        }

        private void setTitle(MetaDataValue mdValue) {
            String metaDataValueDescription = mdValue.getDescription();
            String title = Bundle.getString(ShowThumbnails.class, "MiscMetadataItemSelectedController.AppFrame.Title.Metadata.Value", metaDataValueDescription);
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
        }

        private void setTitle(MetaDataValue mdValue, Object userObject) {
            String userObjectAsString = userObject.toString();
            String metaDataValueDescription = mdValue.getDescription();
            String title = Bundle.getString(ShowThumbnails.class, "MiscMetadataItemSelectedController.AppFrame.Title.Metadata.Object", metaDataValueDescription, userObjectAsString);
            MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            mainWindowManager.setMainWindowTitle(title);
        }
    }
}
