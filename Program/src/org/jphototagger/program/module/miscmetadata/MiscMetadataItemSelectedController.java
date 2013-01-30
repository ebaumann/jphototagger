package org.jphototagger.program.module.miscmetadata;

import java.io.File;
import java.util.Collections;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifMetaDataValues;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class MiscMetadataItemSelectedController implements TreeSelectionListener {

    private TreePath selectedPath;

    public MiscMetadataItemSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getMiscMetadataTree().addTreeSelectionListener(this);
    }

    private boolean isItemSelected() {
        return GUI.getMiscMetadataTree().getSelectionCount() == 1;
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        selectedPath = null;
        if (evt.isAddedPath()) {
            selectedPath = evt.getNewLeadSelectionPath();
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(selectedPath, null));
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (evt.getOriginOfDisplayedThumbnails().isFilesMatchingMiscMetadata() && isItemSelected()) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(selectedPath, evt.getThumbnailsPanelSettings()));
        }
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(ExifInsertedEvent evt) {
        if (selectedPath != null) {
            updateExif(evt.getExif());
        }
    }

    @EventSubscriber(eventClass = ExifDeletedEvent.class)
    public void exifDeleted(ExifDeletedEvent evt) {
        if (selectedPath != null) {
            updateExif(evt.getExif());
        }
    }

    private boolean updateExif(Exif exif) {
        SelectedValue selectedValue = getSelectedValue();
        if (selectedValue == null) {
            return true;
        }
        if (!ExifMetaDataValues.isExifMetadataValue(selectedValue.mdValue)) {
            return true;
        }
        if (exif.getValue(selectedValue.mdValue) != null) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(selectedPath, null));
        }
        return false;
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        if (selectedPath != null) {
            updateXmp(evt.getXmp());
        }
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        if (selectedPath != null) {
            updateXmp(evt.getXmp());
        }
    }

    private boolean updateXmp(Xmp xmp) {
        SelectedValue selectedValue = getSelectedValue();
        if (selectedValue == null) {
            return true;
        }
        if (!XmpMetaDataValues.isXmpMetaDataValue(selectedValue.mdValue)) {
            return true;
        }
        if (xmp.getValue(selectedValue.mdValue) != null) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(selectedPath, null));
        }
        return false;
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        if (selectedPath == null) {
            return;
        }
        SelectedValue selectedValue = getSelectedValue();
        if (selectedValue == null) {
            return;
        }
        if (!XmpMetaDataValues.isXmpMetaDataValue(selectedValue.mdValue)) {
            return;
        }
        Xmp oldXmp = evt.getOldXmp();
        Xmp updatedXmp = evt.getUpdatedXmp();
        Object oldValue = oldXmp.getValue(selectedValue.mdValue);
        Object newValue = updatedXmp.getValue(selectedValue.mdValue);
        boolean valueSelected = !selectedValue.isMetadataCategory && selectedValue.value != null;
        boolean update = valueSelected && !ObjectUtil.equals(selectedValue.value, newValue)
                || !ObjectUtil.equals(oldValue, newValue);
        if (update) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails(selectedPath, null));
        }
    }

    private static final class SelectedValue {

        private final MetaDataValue mdValue;
        private final boolean isMetadataCategory;
        private final Object value;

        private SelectedValue(MetaDataValue mdValue, boolean isMetadataCategory, Object value) {
            this.mdValue = mdValue;
            this.isMetadataCategory = isMetadataCategory;
            this.value = value;
        }
    }

    private SelectedValue getSelectedValue() {
        if (selectedPath == null) {
            return null;
        }
        Object lpc = selectedPath.getLastPathComponent();
        if (!(lpc instanceof DefaultMutableTreeNode)) {
            return null;
        }
        DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) lpc;
        Object userObject = selNode.getUserObject();
        Object parentUserObject = ((DefaultMutableTreeNode) selNode.getParent()).getUserObject();
        boolean isMetadataCategory = userObject instanceof MetaDataValue;
        boolean containsValue = parentUserObject instanceof MetaDataValue;
        if (!isMetadataCategory && !containsValue) {
            return null;
        }
        MetaDataValue md5Value = isMetadataCategory
                ? (MetaDataValue) userObject
                : (MetaDataValue) parentUserObject;
        Object value = containsValue
                ? userObject
                : null;
        return new SelectedValue(md5Value, isMetadataCategory, value);
    }

    private static class ShowThumbnails implements Runnable {

        private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
        private final ThumbnailsPanelSettings tnPanelSettings;
        private final TreePath selectedTreePath;

        private ShowThumbnails(TreePath treePath, ThumbnailsPanelSettings settings) {
            this.selectedTreePath = treePath;
            tnPanelSettings = settings;
        }

        @Override
        public void run() {
            if (selectedTreePath == null) {
                return;
            }
            WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
            waitDisplayer.show();
            Object lastPathComponent = selectedTreePath.getLastPathComponent();
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
                    tnPanel.setFiles(Collections.<File>emptyList(), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
                    setTitle();
                }
            } else if (userObject instanceof MetaDataValue) {
                MetaDataValue mdValue = (MetaDataValue) userObject;
                setTitle(mdValue);
                tnPanel.setFiles(repo.findImageFilesContainingAVauleInMetaDataValue(mdValue), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
                tnPanel.applyThumbnailsPanelSettings(tnPanelSettings);
            } else {
                tnPanel.setFiles(Collections.<File>emptyList(), OriginOfDisplayedThumbnails.FILES_MATCHING_MISC_METADATA);
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
