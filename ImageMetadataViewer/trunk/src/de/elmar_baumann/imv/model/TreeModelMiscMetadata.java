package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Misc metadata information.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeModelMiscMetadata extends DefaultTreeModel implements
        DatabaseListener {

    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode(
            Bundle.getString("TreeModelMiscMetadata.Root.DisplayName"));
    private static final List<Column> exifColumns = new ArrayList<Column>();
    private static final Object exifUserObject = Bundle.getString(
            "TreeModelMiscMetadata.ExifNode.DisplayName");
    private final DatabaseImageFiles db;


    static {
        exifColumns.add(ColumnExifRecordingEquipment.INSTANCE);
        exifColumns.add(ColumnExifFocalLength.INSTANCE);
        exifColumns.add(ColumnExifIsoSpeedRatings.INSTANCE);
    }

    public TreeModelMiscMetadata() {
        super(ROOT);
        db = DatabaseImageFiles.INSTANCE;
        addExifInfo();
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    public static boolean containsExifColumn(Column column) {
        return exifColumns.contains(column);
    }

    private void addExifInfo() {
        DefaultMutableTreeNode exifNode = new DefaultMutableTreeNode(
                exifUserObject);
        for (Column column : exifColumns) {
            DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(
                    column);
            addChildren(columnNode, db.getAllDistinctValues(column),
                    column.getDataType());
            exifNode.add(columnNode);
        }
        ROOT.add(exifNode);
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
            } else {
                assert false : "Unregognized data type: " + dataType;
            }
            parentNode.add(node);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (action.getType().equals(DatabaseAction.Type.IMAGEFILE_INSERTED)) {
            checkImageInserted(action.getImageFileData());
        }
    }

    private void checkImageInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            checkExifInserted(exif);
        }
    }

    private void checkExifInserted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();
        if (recordingEquipment != null) {
            checkExifRecordingEquipmentInserted(recordingEquipment);
        }
    }

    private void checkExifRecordingEquipmentInserted(String recordingEquipment) {
        DefaultMutableTreeNode node =
                getColumnNode(ColumnExifRecordingEquipment.INSTANCE);
        if (node != null &&
                !existsChildUserObjectString(node, recordingEquipment)) {
            DefaultMutableTreeNode child =
                    new DefaultMutableTreeNode(recordingEquipment);
            node.add(child);
            nodesWereInserted(node, new int[]{node.getIndex(child)});
        }
    }

    private boolean existsChildUserObjectString(DefaultMutableTreeNode node,
            String string) {
        boolean exists = false;
        Enumeration children = node.children();
        while (!exists && children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.
                    nextElement();
            Object userObject = child.getUserObject();
            if (userObject instanceof String) {
                String s = (String) userObject;
                exists = s.equals(string);
            }
        }
        return exists;
    }

    private DefaultMutableTreeNode getColumnNode(Column column) {
        DefaultMutableTreeNode exifNode = null;
        Enumeration rootChildren = ROOT.children();
        while (exifNode == null && rootChildren.hasMoreElements()) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) rootChildren.nextElement();
            Object userObject = child.getUserObject();
            if (userObject.equals(exifUserObject)) {
                exifNode = child;
            }
        }
        if (exifNode == null) return null;
        Enumeration exifNodeChildren = exifNode.children();
        DefaultMutableTreeNode columnNode = null;
        while (columnNode == null && exifNodeChildren.hasMoreElements()) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) exifNodeChildren.nextElement();
            Object userObject = child.getUserObject();
            if (userObject.equals(column)) {
                columnNode = child;
            }

        }
        return columnNode;
    }
}
