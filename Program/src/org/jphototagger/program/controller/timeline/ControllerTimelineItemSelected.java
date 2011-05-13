package org.jphototagger.program.controller.timeline;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.data.Timeline;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerTimelineItemSelected implements TreeSelectionListener, RefreshListener {
    public ControllerTimelineItemSelected() {
        listen();
    }

    private void listen() {
        GUI.getTimelineTree().addTreeSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.TIMELINE);
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getTimelineTree().getSelectionCount() == 1) {
            setFilesOfTreePathToThumbnailsPanel(GUI.getTimelineTree().getSelectionPath(), evt.getSettings());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            setFilesOfTreePathToThumbnailsPanel(evt.getNewLeadSelectionPath(), null);
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(final TreePath path, final ThumbnailsPanel.Settings settings) {
        if (path != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object lastPathComponent = path.getLastPathComponent();

                    EventQueueUtil.invokeInDispatchThread(new Runnable() {
                        @Override
                        public void run() {
                            WaitDisplay.show();
                            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
                            GUI.getThumbnailsPanel().apply(settings);
                            WaitDisplay.hide();
                        }
                    });
                }
            }, "JPhotoTagger: Setting files of sel. timeline item to TN panel");

            thread.start();
        }
    }

    private void setFilesOfPossibleNodeToThumbnailsPanel(Object lastPathComponent) {
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            setFilesOfNodeToThumbnailsPanel((DefaultMutableTreeNode) lastPathComponent);
        }
    }

    private void setFilesOfNodeToThumbnailsPanel(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (node.equals(Timeline.getUnknownNode())) {
            setTitle();
            ControllerSortThumbnails.setLastSort();
            GUI.getThumbnailsPanel().setFiles(DatabaseImageFiles.INSTANCE.getFilesOfUnknownDate(), Content.TIMELINE);
        } else if (userObject instanceof Timeline.Date) {
            Timeline.Date date = (Timeline.Date) userObject;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

            if (parent != null) {
                boolean isYear = parent.equals(node.getRoot());
                boolean isMonth = !isYear && (node.getChildCount() > 0);
                int month = isYear
                            ? -1
                            : date.month;
                int day = isMonth
                          ? -1
                          : date.day;

                setTitle(isYear, date.year, isMonth, month, date);

                List<File> files = new ArrayList<File>(DatabaseImageFiles.INSTANCE.getFilesOf(date.year, month, day));

                ControllerSortThumbnails.setLastSort();
                GUI.getThumbnailsPanel().setFiles(files, Content.TIMELINE);
            }
        }
    }

    private void setTitle() {
        GUI.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString("ControllerTimelineItemSelected.AppFrame.Title.Timline.Unknown"));
    }

    private void setTitle(boolean isYear, int year, boolean isMonth, int month, Timeline.Date date) {
        java.util.Date d = null;

        if (date.isComplete()) {
            DateFormat df = new SimpleDateFormat("y-M-d");

            try {
                d = df.parse(Integer.toString(date.year) + "-" + Integer.toString(date.month) + "-"
                             + Integer.toString(date.day));
            } catch (Exception ex) {
                AppLogger.logSevere(getClass(), ex);
            }
        }

        NumberFormat yf = new DecimalFormat("####");
        DateFormat mf = new SimpleDateFormat("MMMMM");
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        Object fDate = isYear
                       ? yf.format(year)
                       : isMonth
                         ? mf.format(month) + " " + yf.format(year)
                         : (d == null)
                           ? ""
                           : df.format(d);

        GUI.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString("ControllerTimelineItemSelected.AppFrame.Title.Timeline.Date", fDate));
    }
}
