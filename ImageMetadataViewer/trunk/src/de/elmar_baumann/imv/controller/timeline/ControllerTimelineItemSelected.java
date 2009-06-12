package de.elmar_baumann.imv.controller.timeline;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.util.Calendar;
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
public final class ControllerTimelineItemSelected implements
        TreeSelectionListener, RefreshListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree treeTimeline = appPanel.getTreeTimeline();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();

    public ControllerTimelineItemSelected() {
        listen();
    }

    private void listen() {
        treeTimeline.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.TIMELINE);
    }

    @Override
    public void refresh() {
        if (treeTimeline.getSelectionCount() == 1) {
            setFilesOfTreePathToThumbnailsPanel(treeTimeline.
                    getLeadSelectionPath());
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(TreePath path) {
        if (path != null) {
            Object lastPathComponent = path.getLastPathComponent();
            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
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
        Calendar cal = (Calendar) node.getUserObject();
        boolean isYear = node.getParent().equals(node.getRoot());
        boolean isMonth = !isYear && node.getChildCount() > 0;
        setFilesToThumbnailsPanel(cal.get(Calendar.YEAR),
                isYear
                ? -1
                : cal.get(Calendar.MONTH) + 1,
                isMonth
                ? -1
                : cal.get(Calendar.DAY_OF_MONTH));
    }

    private void setFilesToThumbnailsPanel(int year, int month, int day) {
        thumbnailsPanel.setFiles(db.getFilesOf(year, month, day),
                Content.TIMELINE);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getNewLeadSelectionPath();
        setFilesOfTreePathToThumbnailsPanel(path);
    }
}
