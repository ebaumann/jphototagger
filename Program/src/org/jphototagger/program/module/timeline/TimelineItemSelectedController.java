package org.jphototagger.program.module.timeline;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.timeline.Timeline.Date;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class TimelineItemSelectedController implements TreeSelectionListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private TreePath selectedItemPath;

    public TimelineItemSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getTimelineTree().addTreeSelectionListener(this);
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (selectedItemPath != null) {
            OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
            if (origin.isFilesMatchingDatesInATimeline()) {
                setFilesToThumbnailsPanel(evt.getThumbnailsPanelSettings());
            }
        }
    }

    private void setFilesToThumbnailsPanel(final ThumbnailsPanelSettings settings) {
        if (selectedItemPath == null) {
            return;
        }
        final TreePath path = selectedItemPath;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Object lastPathComponent = path.getLastPathComponent();
                    EventQueueUtil.invokeInDispatchThread(new Runnable() {
                        @Override
                        public void run() {
                            WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                            waitDisplayer.show();
                            setFilesOfPathToThumbnailsPanel(lastPathComponent);
                            GUI.getThumbnailsPanel().applyThumbnailsPanelSettings(settings);
                            waitDisplayer.hide();
                        }
                    });
                }
            }, "JPhotoTagger: Setting files of sel. timeline item to TN panel");
            thread.start();
    }

    private void setFilesOfPathToThumbnailsPanel(Object lastPathComponent) {
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            setFilesOfNodeToThumbnailsPanel((DefaultMutableTreeNode) lastPathComponent);
        }
    }

    private void setFilesOfNodeToThumbnailsPanel(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (node.equals(Timeline.getUnknownNode())) {
            setTitle();
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
                List<File> files = new ArrayList<>(repo.findImageFilesOfDateTaken(date.year, month, day));
                GUI.getThumbnailsPanel().setFiles(files, OriginOfDisplayedThumbnails.FILES_MATCHING_DATES_IN_A_TIMELINE);
            }
        }
    }

    private void setTitle() {
        String title = Bundle.getString(TimelineItemSelectedController.class, "TimelineItemSelectedController.AppFrame.Title.Timline.Unknown");
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(title);
    }

    private void setTitle(boolean isYear, int year, boolean isMonth, int month, Timeline.Date date) {
        java.util.Date d = null;
        if (date.isComplete()) {
            DateFormat df = new SimpleDateFormat("y-M-d");
            try {
                d = df.parse(Integer.toString(date.year) + "-" + Integer.toString(date.month) + "-" + Integer.toString(date.day));
            } catch (Throwable t) {
                Logger.getLogger(TimelineItemSelectedController.class.getName()).log(Level.SEVERE, null, t);
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
        String title = Bundle.getString(TimelineItemSelectedController.class, "TimelineItemSelectedController.AppFrame.Title.Timeline.Date", fDate);
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
        mainWindowManager.setMainWindowTitle(title);
    }

    private boolean isDisplayed(File imageFile) {
        return GUI.getThumbnailsPanel().containsFile(imageFile);
    }

    private boolean isUnknownNodeSelected() {
        if (selectedItemPath == null) {
            return false;
        }
        Object lastPathComponent = selectedItemPath.getLastPathComponent();
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            return ((DefaultMutableTreeNode) lastPathComponent).equals(Timeline.getUnknownNode());
        }
        return false;
    }

    private Timeline.Date getSelectedDate() {
        if (selectedItemPath == null) {
            return null;
        }
        Object lastPathComponent = selectedItemPath.getLastPathComponent();
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
            if (userObject instanceof Timeline.Date) {
                return (Date) userObject;
            }
        }
        return null;
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        selectedItemPath = evt.isAddedPath()
                ? evt.getNewLeadSelectionPath()
                : null;
        setFilesToThumbnailsPanel(null);
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        xmpModified(evt.getImageFile(), evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        xmpModified(evt.getImageFile(), evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        checkDeleted(evt.getImageFile());
    }

    private void checkDeleted(File imageFile) {
        if (selectedItemPath != null) {
            boolean displayed = isDisplayed(imageFile);
            boolean unknownNodeSelected = isUnknownNodeSelected();
            if (displayed && !unknownNodeSelected || !displayed && unknownNodeSelected) {
                setFilesToThumbnailsPanel(createThumbnailsPanelSettings());
            }
        }
    }

    private void xmpModified(File imageFile, Xmp xmp) {
        if (selectedItemPath != null && isUpdate(imageFile, xmp)) {
            setFilesToThumbnailsPanel(createThumbnailsPanelSettings());
        }
    }

    private boolean isUpdate(File imageFile, Xmp xmp) {
        boolean displayed = isDisplayed(imageFile);
        String dateCreated = (String) xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        boolean hasDate = dateCreated != null;
        boolean unknownNodeSelected = isUnknownNodeSelected();
        if (unknownNodeSelected && displayed && !hasDate) {
            return false;
        }
        if (unknownNodeSelected && displayed && hasDate) {
            return true;
        }
        Date selectedDate = getSelectedDate();
        boolean dateSelected = selectedDate != null;
        boolean dateEqualsSelected = false;
        if (dateCreated != null && selectedDate != null) {
            Date xmpDate = new Date(1800, 1, 1);
            xmpDate.setXmpDateCreated(dateCreated);
            dateEqualsSelected = xmpDate.equals(selectedDate);
        }
        if (dateSelected && displayed && dateEqualsSelected) {
            return false;
        }
        if (dateSelected && !displayed && dateEqualsSelected || dateSelected && displayed && !dateEqualsSelected) {
            return true;
        }
        return true;
    }

    @EventSubscriber(eventClass=ExifUpdatedEvent.class)
    public void exifUpdated(ExifUpdatedEvent evt) {
        if (selectedItemPath != null && isUpdate(evt.getImageFile(), evt.getUpdatedExif())) {
            setFilesToThumbnailsPanel(createThumbnailsPanelSettings());
        }
    }

    @EventSubscriber(eventClass=ExifInsertedEvent.class)
    public void exifInserted(ExifInsertedEvent evt) {
        if (selectedItemPath != null && isUpdate(evt.getImageFile(), evt.getExif())) {
            setFilesToThumbnailsPanel(createThumbnailsPanelSettings());
        }
    }

    @EventSubscriber(eventClass=ExifDeletedEvent.class)
    public void exifDeleted(ExifDeletedEvent evt) {
        checkDeleted(evt.getImageFile());
    }

    private boolean isUpdate(File imageFile, Exif exif) {
        boolean displayed = GUI.getThumbnailsPanel().containsFile(imageFile);
        java.sql.Date exifDate = exif.getDateTimeOriginal();
        boolean hasDate = exifDate != null;
        boolean unknownNodeSelected = isUnknownNodeSelected();
        if (unknownNodeSelected && displayed && !hasDate) {
            return false;
        }
        if (unknownNodeSelected && displayed && hasDate) {
            return true;
        }
        Date selectedDate = getSelectedDate();
        boolean dateSelected = selectedDate != null;
        boolean dateEqualsSelected = false;
        if (exifDate != null && selectedDate != null) {
            dateEqualsSelected = selectedDate.equals(new Date(exifDate));
        }
        if (dateSelected && displayed && dateEqualsSelected) {
            return false;
        }
        if (dateSelected && !displayed && dateEqualsSelected || dateSelected && displayed && !dateEqualsSelected) {
            return true;
        }
        return true;
    }

    public ThumbnailsPanelSettings createThumbnailsPanelSettings() {
        ThumbnailsPanelSettings settings = new ThumbnailsPanelSettings(GUI.getThumbnailsPanel().getViewPosition(), Collections.<Integer>emptyList());
        settings.setSelectedFiles(GUI.getThumbnailsPanel().getSelectedFiles());
        return settings;
}
}
