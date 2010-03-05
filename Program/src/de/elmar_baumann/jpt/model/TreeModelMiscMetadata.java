/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifLens;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * This model contains distinct values of specific EXIF and XMP database columns.
 *
 * Elements are {@link DefaultMutableTreeNode}s with the user objects listed below.
 *
 * <ul>
 * <li>The root user object is a {@link String}</li>
 * <li>User objects direct below the root are {@link Column}s</li>
 * <li>User objects below the columns having the data type of the column
 *    ({@link Column#getDataType()}</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeModelMiscMetadata
        extends    DefaultTreeModel
        implements DatabaseImageFilesListener {

    private static final    DefaultMutableTreeNode ROOT                = new DefaultMutableTreeNode(JptBundle.INSTANCE.getString("TreeModelMiscMetadata.Root.DisplayName"));
    private static final    Object                 EXIF_USER_OBJECT    = JptBundle.INSTANCE.getString("TreeModelMiscMetadata.ExifNode.DisplayName");
    private static final    Object                 XMP_USER_OBJECT     = JptBundle.INSTANCE.getString("TreeModelMiscMetadata.XmpNode.DisplayName");
    private static final    Set<Column>            EXIF_COLUMNS        = new LinkedHashSet<Column>();
    private static final    Set<Column>            XMP_COLUMNS         = new LinkedHashSet<Column>();
    private static final    Set<Object>            COLUMN_USER_OBJECTS = new LinkedHashSet<Object>();
    private static final    long                   serialVersionUID    = 2498087635943355657L;
    private final transient DatabaseImageFiles     db;


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

    public TreeModelMiscMetadata() {
        super(ROOT);
        db = DatabaseImageFiles.INSTANCE;
        addColumnNodes(EXIF_USER_OBJECT, EXIF_COLUMNS);
        addColumnNodes(XMP_USER_OBJECT, XMP_COLUMNS);
        listen();
    }

    private void listen() {
        db.addListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {

        DatabaseImageFilesEvent.Type eventType = event.getType();

        if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_INSERTED)) {
            checkImageInserted(event.getImageFile());

        } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED) &&
                   event.getOldImageFile() != null
                   ) {
            checkImageDeleted(event.getOldImageFile());

        } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_UPDATED)) {

            ImageFile imageFile = event.getImageFile();

            if (imageFile != null &&
               (imageFile.isInsertExifIntoDb() || imageFile.isInsertXmpIntoDb())) {

                checkImageInserted(event.getImageFile());
                if (event.getOldImageFile() != null) {
                    checkImageDeleted(event.getOldImageFile());
                }
            }
        } else if (eventType.equals(DatabaseImageFilesEvent.Type.EXIF_UPDATED)) {

            if (event.getImageFile()    != null) checkImageInserted(event.getImageFile());
            if (event.getOldImageFile() != null) checkImageDeleted (event.getOldImageFile());
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
            DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(column);
            addChildren(columnNode, db.getAllDistinctValuesOf(column), column.getDataType());
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
        if (node != null && !db.exists(column, userObject)) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node, userObject);
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
        return foundNodes.size() > 0
               ? foundNodes.get(0)
               : null;
    }
}
