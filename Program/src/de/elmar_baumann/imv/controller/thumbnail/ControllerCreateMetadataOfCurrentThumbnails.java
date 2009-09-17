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
package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.AutomaticTask;
import de.elmar_baumann.imv.view.panels.ProgressBarAutomaticTasks;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.EnumSet;

/**
 * Listens to the {@link ThumbnailsPanel} and when the displayed
 * thumbnails were changed ({@link ThumbnailsPanelListener#thumbnailsChanged()})
 * this controller gives the new displayed files to an
 * {@link InsertImageFilesIntoDatabase} object which updates the database when
 * the displayed image files or XMP sidecar files are newer than their
 * metadata and thumbnails stored in the database.
 *
 * Runs as a {@link AutomaticTask}, that means if an other automatic task is
 * started, the update will be cancelled.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ControllerCreateMetadataOfCurrentThumbnails
        implements ThumbnailsPanelListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerCreateMetadataOfCurrentThumbnails() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public synchronized void thumbnailsChanged() {
        updateMetadata();
    }

    private synchronized void updateMetadata() {
        AppLog.logInfo(getClass(),
                "ControllerCreateMetadataOfCurrentThumbnails.Info.Update"); // NOI18N
        AutomaticTask.INSTANCE.setTask(new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(thumbnailsPanel.getFiles()),
                EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE),
                ProgressBarAutomaticTasks.INSTANCE));
    }

    @Override
    public void thumbnailsSelectionChanged() {
        // ignore
    }
}
