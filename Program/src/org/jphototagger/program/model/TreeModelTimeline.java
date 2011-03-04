package org.jphototagger.program.model;

import org.jphototagger.lib.model.TreeModelUpdateInfo;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Timeline;
import org.jphototagger.program.data.Timeline.Date;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;

import java.awt.EventQueue;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
public final class TreeModelTimeline extends DefaultTreeModel implements DatabaseImageFilesListener {
    private static final long serialVersionUID = 3932797263824188655L;
    private final transient Timeline timeline;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        timeline = DatabaseImageFiles.INSTANCE.getTimeline();
        setRoot(timeline.getRoot());
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void checkDeleted(Xmp xmp) {
        Object o = xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        String xmpDate = (o == null)
                         ? null
                         : (String) xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        boolean xmpDateExists = (xmpDate != null) && DatabaseImageFiles.INSTANCE.existsXMPDateCreated(xmpDate);

        if (!xmpDateExists && (xmpDate != null)) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);

            if (date.isValid() &&!DatabaseImageFiles.INSTANCE.existsXMPDateCreated(xmpDate)) {
                delete(date);
            }
        }
    }

    private void checkDeleted(Exif exif) {
        java.sql.Date exifDate = exif.getDateTimeOriginal();
        boolean exifDateExists = (exifDate != null) && DatabaseImageFiles.INSTANCE.existsExifDate(exifDate);

        if (!exifDateExists && (exifDate != null)) {
            Timeline.Date date = new Timeline.Date(exifDate);

            if (!DatabaseImageFiles.INSTANCE.existsExifDate(exifDate)) {
                delete(date);
            }
        }
    }

    private void checkInserted(Xmp xmp) {
        if (xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)) {
            String xmpDate = (String) xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
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

    @Override
    public void xmpInserted(File imageFile, final Xmp xmp) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkInserted(xmp);
            }
        });
    }

    @Override
    public void xmpUpdated(File imageFile, final Xmp oldXmp, final Xmp updatedXmp) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(oldXmp);
                checkInserted(updatedXmp);
            }
        });
    }

    @Override
    public void xmpDeleted(File imageFile, final Xmp xmp) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(xmp);
            }
        });
    }

    @Override
    public void exifInserted(File imageFile, final Exif exif) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkInserted(exif);
            }
        });
    }

    @Override
    public void exifUpdated(File imageFile, final Exif oldExif, final Exif updatedExif) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(oldExif);
                checkInserted(updatedExif);
            }
        });
    }

    @Override
    public void exifDeleted(File imageFile, final Exif exif) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(exif);
            }
        });
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }
}
