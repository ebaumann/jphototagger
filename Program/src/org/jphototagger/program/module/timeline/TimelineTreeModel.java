package org.jphototagger.program.module.timeline;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

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
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.timeline.Timeline.Date;
import org.jphototagger.lib.swing.UpdateInfoTreeModel;

/**
 *
 * The model contains a {@code Timeline} retrieved through the repository.
 *
 * Elements are {@code DefaultMutableTreeNode}s with the user objects listed
 * below.
 *
 * <ul>
 * <li>The root user object is a {@code String}</li>
 * <li>All other user objects are {@code Date} objects</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class TimelineTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final transient Timeline timeline;

    public TimelineTreeModel() {
        super(new DefaultMutableTreeNode());
        timeline = repo.findTimeline();
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
        UpdateInfoTreeModel.NodeAndChild info = timeline.removeDay(date);

        nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(), info.getUpdatedChild());
    }

    private void insert(Date date) {
        if (!timeline.existsDate(date)) {
            UpdateInfoTreeModel.NodesAndChildIndices info = timeline.add(date);

            for (UpdateInfoTreeModel.NodeAndChildIndices node : info.getInfo()) {
                nodesWereInserted(node.getNode(), node.getChildIndices());
            }
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(final XmpInsertedEvent evt) {
        checkInserted(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(final XmpUpdatedEvent evt) {
        checkDeleted(evt.getOldXmp());
        checkInserted(evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(final XmpDeletedEvent evt) {
        checkDeleted(evt.getXmp());
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(final ExifInsertedEvent evt) {
        checkInserted(evt.getExif());
    }

    @EventSubscriber(eventClass = ExifUpdatedEvent.class)
    public void exifUpdated(final ExifUpdatedEvent evt) {
        checkDeleted(evt.getOldExif());
        checkInserted(evt.getUpdatedExif());
    }

    @EventSubscriber(eventClass = ExifDeletedEvent.class)
    public void exifDeleted(final ExifDeletedEvent evt) {
        checkDeleted(evt.getExif());
    }
}
