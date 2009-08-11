package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.AutomaticTask;
import de.elmar_baumann.imv.view.panels.ProgressBarAutomaticTasks;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.EnumSet;

/**
 * Listens to the {@link ImageFileThumbnailsPanel} and when the displayed
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
    private final ImageFileThumbnailsPanel thumbnailsPanel =
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
        AppLog.logInfo(getClass(), Bundle.getString(
                "ControllerCreateMetadataOfCurrentThumbnails.Info.Update"));
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
