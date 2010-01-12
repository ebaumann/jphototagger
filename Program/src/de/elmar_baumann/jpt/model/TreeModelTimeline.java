/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Timeline;
import de.elmar_baumann.jpt.data.Timeline.Date;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A {@link de.elmar_baumann.jpt.data.Timeline}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeModelTimeline extends DefaultTreeModel implements DatabaseImageFilesListener {

    private static final long               serialVersionUID = 3932797263824188655L;
    private final        Timeline           timeline;
    private final        DatabaseImageFiles db;

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
            xmpDate       = xmp.getIptc4XmpCoreDateCreated();
            xmpDateExists = xmpDate != null && db.existsXMPDateCreated(xmpDate);
        }

        if (!exifDateExists && exifDate != null) {
            Timeline.Date date = new Timeline.Date(exifDate);

            if (!db.existsExifDate(exifDate)) {
                removeDate(date);
            }
        }

        if (!xmpDateExists && xmpDate != null) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);
            if (date.isValid() && !db.existsXMPDateCreated(xmpDate)) {
                removeDate(date);
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
                setDate(date);
            }
        }

        if (xmp != null && xmp.getIptc4XmpCoreDateCreated() != null) {
            String        xmpDate = xmp.getIptc4XmpCoreDateCreated();
            Timeline.Date date    = new Timeline.Date(-1, -1, -1);

            date.setXmpDateCreated(xmpDate);
            if (date.isValid()) {
                setDate(date);
            }
        }
    }

    public void removeDate(Date date) {
        TreeModelUpdateInfo.NodeAndChild info = timeline.removeDay(date);
        nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(), info.getUpdatedChild());
    }

    public void setDate(Date date) {
        if (!timeline.existsDate(date)) {

            TreeModelUpdateInfo.NodesAndChildIndices info = timeline.add(date);

            for (TreeModelUpdateInfo.NodeAndChildIndices node : info.getInfo()) {

                nodesWereInserted(node.getNode(), node.getChildIndices());
            }
        }
    }
}
