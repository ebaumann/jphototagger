/*
 * @(#)TreeModelMiscMetadata.java    Created on 2009-06-12
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif
    .ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifLens;
import org.jphototagger.program.database.metadata.exif
    .ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
 * @author  Elmar Baumann
 */
public final class TreeModelMiscMetadata extends DefaultTreeModel
        implements DatabaseImageFilesListener {
    private static final Object EXIF_USER_OBJECT =
        JptBundle.INSTANCE.getString(
            "TreeModelMiscMetadata.ExifNode.DisplayName");
    private static final long   serialVersionUID = 2498087635943355657L;
    private static final Object XMP_USER_OBJECT =
        JptBundle.INSTANCE.getString(
            "TreeModelMiscMetadata.XmpNode.DisplayName");
    private static final Set<Column> XMP_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Column> EXIF_COLUMNS = new LinkedHashSet<Column>();
    private static final Set<Object> COLUMN_USER_OBJECTS =
        new LinkedHashSet<Object>();

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

    private final transient DatabaseImageFiles db;
    private final boolean                      onlyXmp;
    private final DefaultMutableTreeNode       ROOT;

    public TreeModelMiscMetadata(boolean onlyXmp) {
        super(new DefaultMutableTreeNode(
            JptBundle.INSTANCE.getString(
                "TreeModelMiscMetadata.Root.DisplayName")));
        this.onlyXmp = onlyXmp;
        this.ROOT    = (DefaultMutableTreeNode) getRoot();
        db           = DatabaseImageFiles.INSTANCE;

        if (!onlyXmp) {
            addColumnNodes(EXIF_USER_OBJECT, EXIF_COLUMNS);
        }

        addColumnNodes(XMP_USER_OBJECT, XMP_COLUMNS);
        listen();
    }

    private void listen() {
        db.addListener(this);
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

            addChildren(columnNode, db.getAllDistinctValuesOf(column),
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

        String lens = exif.getLens();

        if (lens != null) {
            checkDeleted(ColumnExifLens.INSTANCE, lens);
        }
    }

    private void checkDeleted(Column column, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, column);

        if ((node != null) &&!db.exists(column, userObject)) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node,
                                               userObject);

            if (child != null) {
                int index = node.getIndex(child);

                node.remove(index);
                nodesWereRemoved(node, new int[] { index },
                                 new Object[] { child });
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

        String lens = exif.getLens();

        if (lens != null) {
            checkInserted(ColumnExifLens.INSTANCE, lens);
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
                nodesWereInserted(node, new int[] { node.getIndex(newChild) });
            }
        }
    }

    private DefaultMutableTreeNode findNodeWithUserObject(
            DefaultMutableTreeNode rootNode, Object userObject) {
        List<DefaultMutableTreeNode> foundNodes =
            new ArrayList<DefaultMutableTreeNode>(1);

        TreeUtil.addNodesUserWithObject(foundNodes, rootNode, userObject, 1);

        return (foundNodes.size() > 0)
               ? foundNodes.get(0)
               : null;
    }

    @Override
    public void xmpUpdated(File imageFile, final Xmp oldXmp,
                           final Xmp updatedXmp) {
        if (oldXmp == null) {
            throw new NullPointerException("oldXmp == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(oldXmp);
                checkInserted(updatedXmp);
            }
        });
    }

    @Override
    public void xmpInserted(File imageFile, final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkInserted(xmp);
            }
        });
    }

    @Override
    public void xmpDeleted(File imageFile, final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(xmp);
            }
        });
    }

    @Override
    public void dcSubjectDeleted(final String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubject);
            }
        });
    }

    @Override
    public void dcSubjectInserted(final String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkInserted(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubject);
            }
        });
    }

    @Override
    public void exifInserted(File imageFile, final Exif exif) {
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkInserted(exif);
            }
        });
    }

    @Override
    public void exifUpdated(File imageFile, final Exif oldExif,
                            final Exif updatedExif) {
        if (oldExif == null) {
            throw new NullPointerException("oldExif == null");
        }

        if (updatedExif == null) {
            throw new NullPointerException("updatedExif == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkDeleted(oldExif);
                checkInserted(updatedExif);
            }
        });
    }

    @Override
    public void exifDeleted(File imageFile, final Exif exif) {
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
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
}
