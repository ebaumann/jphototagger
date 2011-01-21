package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.tasks.AutomaticTask;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

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
 * @author Elmar Baumann
 */
public final class ControllerCreateMetadataOfDisplayedThumbnails
        implements ThumbnailsPanelListener {
    public ControllerCreateMetadataOfDisplayedThumbnails() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
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
                GUI.getThumbnailsPanel().getFiles(), Insert.OUT_OF_DATE);
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
