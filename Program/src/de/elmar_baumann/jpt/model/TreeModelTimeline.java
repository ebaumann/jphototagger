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
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseListener;
import de.elmar_baumann.jpt.event.DatabaseProgramEvent;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A {@link de.elmar_baumann.jpt.data.Timeline}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeModelTimeline extends DefaultTreeModel implements DatabaseListener {

    private final Timeline           timeline;
    private final DatabaseImageFiles db;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        db       = DatabaseImageFiles.INSTANCE;
        timeline = db.getTimeline();
        setRoot(timeline.getRoot());
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        DatabaseImageEvent.Type eventType = event.getType();
        if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_DELETED)) {
            checkDeleted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_INSERTED)) {
            checkInserted(event.getImageFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_UPDATED)) {
            ImageFile imageFile = event.getImageFile();
            if (imageFile != null && imageFile.isInsertExifIntoDb()) {
                checkDeleted(event.getOldImageFile());
                checkInserted(event.getImageFile());
            }
        }
    }

    private void checkDeleted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null && !db.existsExifDay(day)) {
                TreeModelUpdateInfo.NodeAndChild info = timeline.removeDay(new Timeline.Date(day));
                nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(), info.getUpdatedChild());
            }
        }
    }

    private void checkInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null) {
                Timeline.Date date = new Timeline.Date(day);
                if (!timeline.existsDay(date)) {
                    TreeModelUpdateInfo.NodesAndChildIndices info = timeline.add(date);
                    for (TreeModelUpdateInfo.NodeAndChildIndices node : info.getInfo()) {
                        nodesWereInserted(node.getNode(), node.getChildIndices());
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
