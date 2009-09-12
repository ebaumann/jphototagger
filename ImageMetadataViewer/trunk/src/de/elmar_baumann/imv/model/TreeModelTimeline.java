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
package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Timeline;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import java.util.Calendar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A {@link de.elmar_baumann.imv.data.Timeline}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class TreeModelTimeline extends DefaultTreeModel implements
        DatabaseListener {

    private final Timeline timeline;
    private final DatabaseImageFiles db;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        db = DatabaseImageFiles.INSTANCE;
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
                Calendar calDay = Calendar.getInstance();
                calDay.setTime(day);
                TreeModelUpdateInfo.NodeAndChild info = timeline.removeDay(
                        calDay);
                nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(),
                        info.getUpdatedChild());
            }
        }
    }

    private void checkInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null) {
                Calendar calDay = Calendar.getInstance();
                calDay.setTime(day);
                if (!timeline.existsDay(calDay)) {
                    TreeModelUpdateInfo.NodesAndChildIndices info =
                            timeline.add(calDay);
                    for (TreeModelUpdateInfo.NodeAndChildIndices node : info.
                            getInfo()) {
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
