package de.elmar_baumann.imv.controller.timeline;

import de.elmar_baumann.imv.data.Timeline;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.util.Calendar;
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
            setFilesOfTreePathToThumbnailsPanel(treeTimeline.getSelectionPath());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            setFilesOfTreePathToThumbnailsPanel(e.getNewLeadSelectionPath());
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(final TreePath path) {
        if (path != null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    final Object lastPathComponent = path.getLastPathComponent();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            InfoSetThumbnails info = new InfoSetThumbnails();
                            setFilesOfPossibleNodeToThumbnailsPanel(
                                    lastPathComponent);
                            info.hide();
                        }
                    });
                }
            });
            thread.setName("Timeline item selected" + " @ " + // NOI18N
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
        Object userObject = node.getUserObject();
        if (node.equals(Timeline.getUnknownNode())) {
            thumbnailsPanel.setFiles(db.getFilesOfUnknownExifDate(),
                    Content.TIMELINE);
        } else if (userObject instanceof Calendar) {
            Calendar cal = (Calendar) userObject;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.
                    getParent();
            if (parent != null) {
                boolean isYear = parent.equals(node.getRoot());
                boolean isMonth = !isYear && node.getChildCount() > 0;
                int year = cal.get(Calendar.YEAR);
                int month = isYear
                            ? -1
                            : cal.get(Calendar.MONTH) + 1;
                int day = isMonth
                          ? -1
                          : cal.get(Calendar.DAY_OF_MONTH);
                thumbnailsPanel.setFiles(db.getFilesOf(year, month, day),
                        Content.TIMELINE);
            }
        }
    }
}
