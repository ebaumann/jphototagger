package org.jphototagger.program.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.model.TreeModelUpdateInfo;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.timeline.Timeline.Date;
import org.openide.util.Lookup;

/**
 *
 * The model contains a {@link Timeline} retrieved through
 * {@link DatabaseImageFiles#getTimeline()}.
 *
 * Elements are {@link DefaultMutableTreeNode}s with the user objects listed
 * below.
 *
 * <ul>
 * <li>The root user object is a {@link String}</li>
 * <li>All other user objects are {@link Date} objects</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class TreeModelTimeline extends DefaultTreeModel {

    private static final long serialVersionUID = 3932797263824188655L;
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);
    private final transient Timeline timeline;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        timeline = repo.getTimeline();
        setRoot(timeline.getRoot());
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void checkDeleted(Xmp xmp) {
        Object o = xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        String xmpDate = (o == null)
                ? null
                : (String) xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        boolean xmpDateExists = (xmpDate != null) && repo.existsXMPDateCreated(xmpDate);

        if (!xmpDateExists && (xmpDate != null)) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);

            if (date.isValid() && !repo.existsXMPDateCreated(xmpDate)) {
                delete(date);
            }
        }
    }

    private void checkDeleted(Exif exif) {
        java.sql.Date exifDate = exif.getDateTimeOriginal();
        boolean exifDateExists = (exifDate != null) && repo.existsExifDate(exifDate);

        if (!exifDateExists && (exifDate != null)) {
            Timeline.Date date = new Timeline.Date(exifDate);

            if (!repo.existsExifDate(exifDate)) {
                delete(date);
            }
        }
    }

    private void checkInserted(Xmp xmp) {
        if (xmp.contains(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE)) {
            String xmpDate = (String) xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);

            if (date.isValid()) {
                insert(date);
            }
        }
    }

    private void checkInserted(Exif exif) {
        java.sql.Date day = exif.getDateTimeOriginal();

        if (day != null) {
            Timeline.Date date = new Timeline.Date(day);

            insert(date);
        }
    }

    private void delete(Date date) {
        TreeModelUpdateInfo.NodeAndChild info = timeline.removeDay(date);

        nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(), info.getUpdatedChild());
    }

    private void insert(Date date) {
        if (!timeline.existsDate(date)) {
            TreeModelUpdateInfo.NodesAndChildIndices info = timeline.add(date);

            for (TreeModelUpdateInfo.NodeAndChildIndices node : info.getInfo()) {
                nodesWereInserted(node.getNode(), node.getChildIndices());
            }
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(final XmpInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkInserted(evt.getXmp());
            }
        });
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(final XmpUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkDeleted(evt.getOldXmp());
                checkInserted(evt.getUpdatedXmp());
            }
        });
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(final XmpDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkDeleted(evt.getXmp());
            }
        });
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(final ExifInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkInserted(evt.getExif());
            }
        });
    }

    @EventSubscriber(eventClass = ExifUpdatedEvent.class)
    public void exifUpdated(final ExifUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkDeleted(evt.getOldExif());
                checkInserted(evt.getUpdatedExif());
            }
        });
    }

    @EventSubscriber(eventClass = ExifDeletedEvent.class)
    public void exifDeleted(final ExifDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkDeleted(evt.getExif());
            }
        });
    }
}
