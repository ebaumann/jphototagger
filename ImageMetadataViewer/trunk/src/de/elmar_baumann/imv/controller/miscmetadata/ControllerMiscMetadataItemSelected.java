package de.elmar_baumann.imv.controller.miscmetadata;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSettingThumbnails;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
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
 * @version 2009/06/12
 */
public final class ControllerMiscMetadataItemSelected implements
        TreeSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeMiscMetadata();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
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
            InfoSettingThumbnails info = new InfoSettingThumbnails();
            Object lastPathComponent = treePath.getLastPathComponent();
            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
            info.hide();
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
