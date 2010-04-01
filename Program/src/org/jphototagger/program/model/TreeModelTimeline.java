/*
 * @(#)TreeModelTimeline.java    Created on 2009-06-12
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Timeline;
import org.jphototagger.program.data.Timeline.Date;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.lib.model.TreeModelUpdateInfo;

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
 * @author  Elmar Baumann
 */
public final class TreeModelTimeline extends DefaultTreeModel
        implements DatabaseImageFilesListener {
    private static final long                  serialVersionUID =
        3932797263824188655L;
    private final transient DatabaseImageFiles db;
    private final transient Timeline           timeline;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        db       = DatabaseImageFiles.INSTANCE;
        timeline = db.getTimeline();
        setRoot(timeline.getRoot());
        listen();
    }

    private void listen() {
        db.addListener(this);
    }

    private void checkDeleted(Xmp xmp) {
        Object o       =
            xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        String xmpDate = (o == null)
                         ? null
                         : (String) xmp.getValue(
                             ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
        boolean xmpDateExists = (xmpDate != null)
                                && db.existsXMPDateCreated(xmpDate);

        if (!xmpDateExists && (xmpDate != null)) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);

            if (date.isValid() &&!db.existsXMPDateCreated(xmpDate)) {
                delete(date);
            }
        }
    }

    private void checkDeleted(Exif exif) {
        java.sql.Date exifDate       = exif.getDateTimeOriginal();
        boolean       exifDateExists = (exifDate != null)
                                       && db.existsExifDate(exifDate);

        if (!exifDateExists && (exifDate != null)) {
            Timeline.Date date = new Timeline.Date(exifDate);

            if (!db.existsExifDate(exifDate)) {
                delete(date);
            }
        }
    }

    private void checkInserted(Xmp xmp) {
        if (xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)) {
            String xmpDate = (String) xmp.getValue(
                                 ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
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

        nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(),
                         info.getUpdatedChild());
    }

    private void insert(Date date) {
        if (!timeline.existsDate(date)) {
            TreeModelUpdateInfo.NodesAndChildIndices info = timeline.add(date);

            for (TreeModelUpdateInfo
                    .NodeAndChildIndices node : info.getInfo()) {
                nodesWereInserted(node.getNode(), node.getChildIndices());
            }
        }
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        checkInserted(xmp);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (oldXmp == null) {
            throw new NullPointerException("oldXmp == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        checkDeleted(oldXmp);
        checkInserted(updatedXmp);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        checkDeleted(xmp);
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        checkInserted(exif);
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        if (oldExif == null) {
            throw new NullPointerException("oldExif == null");
        }

        if (updatedExif == null) {
            throw new NullPointerException("updatedExif == null");
        }

        checkDeleted(oldExif);
        checkInserted(updatedExif);
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }

        checkDeleted(exif);
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
