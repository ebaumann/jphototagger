package de.elmar_baumann.imv.controller.miscmetadata;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class ControllerMiscMetadataItemSelected implements
        TreeSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeMiscMetadata();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();

    public ControllerMiscMetadataItemSelected() {
        listen();
    }

    private void listen() {
        tree.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.MISC_METADATA);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        setFilesOfTreePathToThumbnailsPanel(e.getNewLeadSelectionPath());
    }

    @Override
    public void refresh() {
        if (tree.getSelectionCount() == 1) {
            setFilesOfTreePathToThumbnailsPanel(tree.getLeadSelectionPath());
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(final TreePath path) {
        if (path != null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    InfoSetThumbnails info = new InfoSetThumbnails();
                    Object lastPathComponent = path.getLastPathComponent();
                    setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
                    info.hide();
                }
            });
            thread.setName("Misc metadata item selected" + " @ " + // NOI18N
                    getClass().getName());
            thread.start();
        }
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
                    ((DefaultMutableTreeNode) node.getParent()).getUserObject();
            if (parentUserObject instanceof Column) {
                Column column = (Column) parentUserObject;
                if (TreeModelMiscMetadata.containsExifColumn(column)) {
                    setFilesToThumbnailsPanelExif(column, userObject.toString());
                }
            }
        } else {
            thumbnailsPanel.setFiles(new ArrayList<File>(),
                    Content.MISC_METADATA);
        }
    }

    private void setFilesToThumbnailsPanelExif(Column column, String value) {
        thumbnailsPanel.setFiles(db.getFilesFromExif(column, value),
                Content.MISC_METADATA);
    }
}
