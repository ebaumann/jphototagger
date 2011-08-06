package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.exif.ColumnExifFocalLength;
import org.jphototagger.domain.database.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.domain.database.exif.ColumnExifLens;
import org.jphototagger.domain.database.exif.ColumnExifRecordingEquipment;
import org.jphototagger.domain.database.xmp.ColumnXmpDcCreator;
import org.jphototagger.domain.database.xmp.ColumnXmpDcRights;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.xmp.ColumnXmpRating;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.repository.event.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseImageFiles;

/**
 * This model contains distinct values of specific EXIF and XMP database
 * columns.
 *
 * Elements are {@link DefaultMutableTreeNode}s with the user objects listed
 * below.
 *
 * <ul>
 * <li>The root user object is a {@link String}</li>
 * <li>User objects direct below the root are {@link Column}s</li>
 * <li>User objects below the columns having the data type of the column
 *    ({@link Column#getDataType()}</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class TreeModelMiscMetadata extends DefaultTreeModel {

    private static final Object EXIF_USER_OBJECT = Bundle.getString(TreeModelMiscMetadata.class, "TreeModelMiscMetadata.ExifNode.DisplayName");
    private static final long serialVersionUID = 2498087635943355657L;
    private static final Object XMP_USER_OBJECT = Bundle.getString(TreeModelMiscMetadata.class, "TreeModelMiscMetadata.XmpNode.DisplayName");
    private static final Set<Column> XMP_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Column> EXIF_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Object> COLUMN_USER_OBJECTS = new LinkedHashSet<Object>();

    static {
        EXIF_COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);
        EXIF_COLUMNS.add(ColumnExifFocalLength.INSTANCE);
        EXIF_COLUMNS.add(ColumnExifLens.INSTANCE);
        EXIF_COLUMNS.add(ColumnExifIsoSpeedRatings.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpRating.INSTANCE);
        COLUMN_USER_OBJECTS.add(EXIF_USER_OBJECT);
        COLUMN_USER_OBJECTS.add(XMP_USER_OBJECT);
    }
    private final boolean onlyXmp;
    private final DefaultMutableTreeNode ROOT;

    public TreeModelMiscMetadata(boolean onlyXmp) {
        super(new DefaultMutableTreeNode(Bundle.getString(TreeModelMiscMetadata.class, "TreeModelMiscMetadata.Root.DisplayName")));
        this.onlyXmp = onlyXmp;
        this.ROOT = (DefaultMutableTreeNode) getRoot();

        if (!onlyXmp) {
            addColumnNodes(EXIF_USER_OBJECT, EXIF_COLUMNS);
        }

        addColumnNodes(XMP_USER_OBJECT, XMP_COLUMNS);
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    public boolean isOnlyXmp() {
        return onlyXmp;
    }

    public static Set<Column> getExifColumns() {
        return new LinkedHashSet<Column>(EXIF_COLUMNS);
    }

    public static Set<Column> getXmpColumns() {
        return new LinkedHashSet<Column>(XMP_COLUMNS);
    }

    private void addColumnNodes(Object userObject, Set<Column> columns) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);

        for (Column column : columns) {
            DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(column);

            addChildren(columnNode, DatabaseImageFiles.INSTANCE.getAllDistinctValuesOf(column), column.getDataType());
            node.add(columnNode);
        }

        ROOT.add(node);
    }

    private void addChildren(DefaultMutableTreeNode parentNode, Set<String> data, Column.DataType dataType) {
        for (String string : data) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode();

            if (dataType.equals(Column.DataType.STRING)) {
                node.setUserObject(string);
            } else if (dataType.equals(Column.DataType.SMALLINT)) {
                node.setUserObject(Short.valueOf(string));
            } else if (dataType.equals(Column.DataType.REAL)) {
                node.setUserObject(Double.valueOf(string));
            } else if (dataType.equals(Column.DataType.BIGINT)) {
                node.setUserObject(Long.valueOf(string));
            } else {
                assert false : "Unregognized data type: " + dataType;
            }

            parentNode.add(node);
        }
    }

    private void checkDeleted(Xmp xmp) {
        for (Column xmpColumn : XMP_COLUMNS) {
            Object value = xmp.getValue(xmpColumn);

            if (value != null) {
                checkDeleted(xmpColumn, value);
            }
        }
    }

    private void checkDeleted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();

        if (recordingEquipment != null) {
            checkDeleted(ColumnExifRecordingEquipment.INSTANCE, recordingEquipment);
        }

        short iso = exif.getIsoSpeedRatings();

        if (iso > 0) {
            checkDeleted(ColumnExifIsoSpeedRatings.INSTANCE, Short.valueOf(iso));
        }

        double f = exif.getFocalLength();

        if (f > 0) {
            checkDeleted(ColumnExifFocalLength.INSTANCE, Double.valueOf(f));
        }

        String lens = exif.getLens();

        if (lens != null) {
            checkDeleted(ColumnExifLens.INSTANCE, lens);
        }
    }

    private void checkDeleted(Column column, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, column);

        if ((node != null) && !DatabaseImageFiles.INSTANCE.exists(column, userObject)) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node, userObject);

            if (child != null) {
                int index = node.getIndex(child);

                node.remove(index);
                nodesWereRemoved(node, new int[]{index}, new Object[]{child});
            }
        }
    }

    private void checkInserted(Xmp xmp) {
        for (Column xmpColumn : XMP_COLUMNS) {
            Object value = xmp.getValue(xmpColumn);

            if (value != null) {
                checkInserted(xmpColumn, value);
            }
        }
    }

    private void checkInserted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();

        if (recordingEquipment != null) {
            checkInserted(ColumnExifRecordingEquipment.INSTANCE, recordingEquipment);
        }

        short iso = exif.getIsoSpeedRatings();

        if (iso > 0) {
            checkInserted(ColumnExifIsoSpeedRatings.INSTANCE, Short.valueOf(iso));
        }

        double f = exif.getFocalLength();

        if (f > 0) {
            checkInserted(ColumnExifFocalLength.INSTANCE, Double.valueOf(f));
        }

        String lens = exif.getLens();

        if (lens != null) {
            checkInserted(ColumnExifLens.INSTANCE, lens);
        }
    }

    private void checkInserted(Column column, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, column);

        if (node != null) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node, userObject);

            if (child == null) {
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(userObject);

                node.add(newChild);
                nodesWereInserted(node, new int[]{node.getIndex(newChild)});
            }
        }
    }

    private DefaultMutableTreeNode findNodeWithUserObject(DefaultMutableTreeNode rootNode, Object userObject) {
        List<DefaultMutableTreeNode> foundNodes = new ArrayList<DefaultMutableTreeNode>(1);

        TreeUtil.addNodesUserWithObject(foundNodes, rootNode, userObject, 1);

        return (foundNodes.size() > 0)
                ? foundNodes.get(0)
                : null;
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

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(final XmpInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkInserted(evt.getXmp());
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

    @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
    public void dcSubjectDeleted(final DcSubjectDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkDeleted(ColumnXmpDcSubjectsSubject.INSTANCE, evt.getDcSubject());
            }
        });
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(final DcSubjectInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                checkInserted(ColumnXmpDcSubjectsSubject.INSTANCE, evt.getDcSubject());
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
