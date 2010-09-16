/*
 * @(#)ControllerCreateMetadataOfDisplayedThumbnails.java    Created on 2008-09-29
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

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.AutomaticTask;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.ViewUtil;

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
 * @author  Elmar Baumann
 */
public final class ControllerCreateMetadataOfDisplayedThumbnails
        implements ThumbnailsPanelListener {
    public ControllerCreateMetadataOfDisplayedThumbnails() {
        listen();
    }

    private void listen() {
        ViewUtil.getThumbnailsPanel().addThumbnailsPanelListener(this);
    }

    @Override
    public synchronized void thumbnailsChanged() {
        updateMetadata();
    }

    private synchronized void updateMetadata() {
        AppLogger.logInfo(
            getClass(),
            "ControllerCreateMetadataOfDisplayedThumbnails.Info.Update");

        InsertImageFilesIntoDatabase inserter =
            new InsertImageFilesIntoDatabase(
                ViewUtil.getThumbnailsPanel().getFiles(), Insert.OUT_OF_DATE);
        String pBarString =
            JptBundle.INSTANCE.getString(
                "ControllerCreateMetadataOfDisplayedThumbnails.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter,
                pBarString));
        AutomaticTask.INSTANCE.setTask(inserter);
    }

    @Override
    public void thumbnailsSelectionChanged() {

        // ignore
    }
}
