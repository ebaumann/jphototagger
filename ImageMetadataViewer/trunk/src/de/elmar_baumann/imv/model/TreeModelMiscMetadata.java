package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Misc metadata information.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeModelMiscMetadata extends DefaultTreeModel implements
        DatabaseListener {

    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode(
            Bundle.getString("TreeModelMiscMetadata.Root.DisplayName")); // NOI18N
    private static final Object EXIF_USER_OBJECT =
            Bundle.getString("TreeModelMiscMetadata.ExifNode.DisplayName"); // NOI18N
    private static final Object XMP_USER_OBJECT =
            Bundle.getString("TreeModelMiscMetadata.XmpNode.DisplayName"); // NOI18N
    private static final Set<Column> EXIF_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Column> XMP_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Object> COLUMN_USER_OBJECTS =
            new LinkedHashSet<Object>();
    private final DatabaseImageFiles db;


    static {
        EXIF_COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);
        EXIF_COLUMNS.add(ColumnExifFocalLength.INSTANCE);
        EXIF_COLUMNS.add(ColumnExifIsoSpeedRatings.INSTANCE);

        XMP_COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        XMP_COLUMNS.add(ColumnXmpRating.INSTANCE);

        COLUMN_USER_OBJECTS.add(EXIF_USER_OBJECT);
        COLUMN_USER_OBJECTS.add(XMP_USER_OBJECT);
    }

    public TreeModelMiscMetadata() {
        super(ROOT);
        db = DatabaseImageFiles.INSTANCE;
        addColumnNodes(EXIF_USER_OBJECT, EXIF_COLUMNS);
        addColumnNodes(XMP_USER_OBJECT, XMP_COLUMNS);
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        DatabaseImageEvent.Type eventType = event.getType();
        if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_INSERTED)) {
            checkImageInserted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_DELETED)) {
            checkImageDeleted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_UPDATED)) {
            ImageFile imageFile = event.getImageFile();
            if (imageFile != null && imageFile.isInsertExifIntoDb()) {
                checkImageInserted(event.getImageFile());
                checkImageDeleted(event.getImageFile());
            }
        }
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
            DefaultMutableTreeNode columnNode =
                    new DefaultMutableTreeNode(column);
            addChildren(columnNode, db.getAllDistinctValues(column),
                    column.getDataType());
            node.add(columnNode);
        }
        ROOT.add(node);
    }

    private void addChildren(DefaultMutableTreeNode parentNode,
            Set<String> data, Column.DataType dataType) {

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
                assert false : "Unregognized data type: " + dataType; // NOI18N
            }
            parentNode.add(node);
        }
    }

    private void checkImageDeleted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            checkExifDeleted(exif);
        }
        Xmp xmp = imageFile.getXmp();
        if (xmp != null) {
            checkXmpDeleted(xmp);
        }
    }

    private void checkXmpDeleted(Xmp xmp) {
        for (Column xmpColumn : XMP_COLUMNS) {
            Object value = xmp.getValue(xmpColumn);
            if (value != null) {
                checkDeleted(xmpColumn, value);
            }
        }
    }

    private void checkExifDeleted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();
        if (recordingEquipment != null) {
            checkDeleted(ColumnExifRecordingEquipment.INSTANCE,
                    recordingEquipment);
        }
        short iso = exif.getIsoSpeedRatings();
        if (iso > 0) {
            checkDeleted(ColumnExifIsoSpeedRatings.INSTANCE,
                    Short.valueOf(iso));
        }
        double f = exif.getFocalLength();
        if (f > 0) {
            checkDeleted(ColumnExifFocalLength.INSTANCE, Double.valueOf(f));
        }
    }

    private void checkDeleted(Column column, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, column);
        if (node != null && !db.exists(column, userObject)) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node,
                    userObject);
            if (child != null) {
                int index = node.getIndex(child);
                node.remove(index);
                nodesWereRemoved(node, new int[]{index}, new Object[]{child});
            }
        }
    }

    private void checkImageInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            checkExifInserted(exif);
        }
        Xmp xmp = imageFile.getXmp();
        if (xmp != null) {
            checkXmpInserted(xmp);
        }
    }

    private void checkXmpInserted(Xmp xmp) {
        for (Column xmpColumn : XMP_COLUMNS) {
            Object value = xmp.getValue(xmpColumn);
            if (value != null) {
                checkInserted(xmpColumn, value);
            }
        }
    }

    private void checkExifInserted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();
        if (recordingEquipment != null) {
            checkInserted(ColumnExifRecordingEquipment.INSTANCE,
                    recordingEquipment);
        }
        short iso = exif.getIsoSpeedRatings();
        if (iso > 0) {
            checkInserted(ColumnExifIsoSpeedRatings.INSTANCE,
                    Short.valueOf(iso));
        }
        double f = exif.getFocalLength();
        if (f > 0) {
            checkInserted(ColumnExifFocalLength.INSTANCE, Double.valueOf(f));
        }
    }

    private void checkInserted(Column column, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, column);
        if (node != null) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node,
                    userObject);
            if (child == null) {
                DefaultMutableTreeNode newChild =
                        new DefaultMutableTreeNode(userObject);
                node.add(newChild);
                nodesWereInserted(node, new int[]{node.getIndex(newChild)});
            }
        }
    }

    private DefaultMutableTreeNode findNodeWithUserObject(
            DefaultMutableTreeNode rootNode, Object userObject) {
        List<DefaultMutableTreeNode> foundNodes =
                new ArrayList<DefaultMutableTreeNode>(1);
        TreeUtil.addNodesUserWithObject(foundNodes, rootNode, userObject, 1);
        return foundNodes.size() > 0
               ? foundNodes.get(0)
               : null;
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }
}
