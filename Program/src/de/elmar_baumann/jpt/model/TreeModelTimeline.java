/*
 * JPhotoTagger tags and finds images fast.
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
import de.elmar_baumann.jpt.data.Timeline;
import de.elmar_baumann.jpt.data.Timeline.Date;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
/**
 * The model contains a {@link Timeline} retrieved through
 * {@link DatabaseImageFiles#getTimeline()}.
 *
 * Elements are {@link DefaultMutableTreeNode}s with the user objects listed below.
 *
 * <ul>
 * <li>The root user object is a {@link String}</li>
 * <li>All other user objects are {@link Date} objects</li>
 * </ul>
 *
 * @author  Elmar Baumann
 * @version 2009-06-12
 */
public final class TreeModelTimeline extends DefaultTreeModel implements DatabaseImageFilesListener {

    private static final    long               serialVersionUID = 3932797263824188655L;
    private final transient Timeline           timeline;
    private final transient DatabaseImageFiles db;

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

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        DatabaseImageFilesEvent.Type eventType = event.getType();
        if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED)) {
            checkDeleted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_INSERTED)) {
            checkInserted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_UPDATED)) {
            ImageFile imageFile = event.getImageFile();
            if (imageFile != null && (imageFile.isInsertExifIntoDb() || imageFile.isInsertXmpIntoDb())) {
                checkDeleted(event.getOldImageFile());
                checkInserted(event.getImageFile());
            }
        }
    }

    private void checkDeleted(ImageFile imageFile) {
        Exif          exif           = imageFile.getExif();
        Xmp           xmp            = imageFile.getXmp();
        java.sql.Date exifDate       = null;
        String        xmpDate        = null;
        boolean       exifDateExists = false;
        boolean       xmpDateExists  = false;

        if (exif != null) {
            exifDate       = exif.getDateTimeOriginal();
            exifDateExists = exifDate != null && db.existsExifDate(exifDate);
        }

        if (xmp != null) {
            Object o = xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
            xmpDate       = o == null ? null : (String) xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
            xmpDateExists = xmpDate != null && db.existsXMPDateCreated(xmpDate);
        }

        if (!exifDateExists && exifDate != null) {
            Timeline.Date date = new Timeline.Date(exifDate);

            if (!db.existsExifDate(exifDate)) {
                delete(date);
            }
        }

        if (!xmpDateExists && xmpDate != null) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);
            if (date.isValid() && !db.existsXMPDateCreated(xmpDate)) {
                delete(date);
            }
        }
    }

    private void checkInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        Xmp  xmp  = imageFile.getXmp();

        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null) {
                Timeline.Date date = new Timeline.Date(day);
                insert(date);
            }
        }

        if (xmp != null && xmp.contains(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)) {
            String        xmpDate = (String)xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE);
            Timeline.Date date    = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);
            if (date.isValid()) {
                insert(date);
            }
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
}
