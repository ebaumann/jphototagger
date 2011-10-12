package org.jphototagger.program.module.timeline;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.SortThumbnailsController;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.app.ui.WaitDisplay;

/**
 * @author Elmar Baumann
 */
public final class TimelineItemSelectedController implements TreeSelectionListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public TimelineItemSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getTimelineTree().addTreeSelectionListener(this);
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getTimelineTree().getSelectionCount() == 1) {
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_MATCHING_DATES_IN_A_TIMELINE.equals(typeOfDisplayedImages)) {
                setFilesOfTreePathToThumbnailsPanel(GUI.getTimelineTree().getSelectionPath(), evt.getThumbnailsPanelSettings());
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            setFilesOfTreePathToThumbnailsPanel(evt.getNewLeadSelectionPath(), null);
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(final TreePath path, final ThumbnailsPanelSettings settings) {
        if (path != null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    final Object lastPathComponent = path.getLastPathComponent();

                    EventQueueUtil.invokeInDispatchThread(new Runnable() {

                        @Override
                        public void run() {
                            WaitDisplay.INSTANCE.show();
                            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
                            GUI.getThumbnailsPanel().apply(settings);
                            WaitDisplay.INSTANCE.hide();
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
            SortThumbnailsController.setLastSort();
            GUI.getThumbnailsPanel().setFiles(repo.findImageFilesOfUnknownDateTaken(), OriginOfDisplayedThumbnails.FILES_MATCHING_DATES_IN_A_TIMELINE);
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

                List<File> files = new ArrayList<File>(repo.findImageFilesOfDateTaken(date.year, month, day));

                SortThumbnailsController.setLastSort();
                GUI.getThumbnailsPanel().setFiles(files, OriginOfDisplayedThumbnails.FILES_MATCHING_DATES_IN_A_TIMELINE);
            }
        }
    }

    private void setTitle() {
        GUI.getAppFrame().setTitle(
                Bundle.getString(TimelineItemSelectedController.class, "TimelineItemSelectedController.AppFrame.Title.Timline.Unknown"));
    }

    private void setTitle(boolean isYear, int year, boolean isMonth, int month, Timeline.Date date) {
        java.util.Date d = null;

        if (date.isComplete()) {
            DateFormat df = new SimpleDateFormat("y-M-d");

            try {
                d = df.parse(Integer.toString(date.year) + "-" + Integer.toString(date.month) + "-"
                        + Integer.toString(date.day));
            } catch (Exception ex) {
                Logger.getLogger(TimelineItemSelectedController.class.getName()).log(Level.SEVERE, null, ex);
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
                Bundle.getString(TimelineItemSelectedController.class, "TimelineItemSelectedController.AppFrame.Title.Timeline.Date", fDate));
    }
}
