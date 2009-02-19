package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ControllerCreateMetadataOfCurrentThumbnails
        implements ThumbnailsPanelListener, ProgressListener {

    private final Queue<InsertImageFilesIntoDatabase> updaters = new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private boolean wait = false;
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final JProgressBar progressBar = appPanel.getProgressBarCreateMetadataOfCurrentThumbnails();
    private boolean stop = false;

    public ControllerCreateMetadataOfCurrentThumbnails() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private synchronized void updateMetadata() {
        updaters.add(createUpdater(FileUtil.getAsFilenames(thumbnailsPanel.getFiles())));
        startUpdateMetadataThread();
    }

    private InsertImageFilesIntoDatabase createUpdater(List<String> files) {
        InsertImageFilesIntoDatabase updater =
                new InsertImageFilesIntoDatabase(files, EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE));
        updater.addProgressListener(this);
        return updater;
    }

    private synchronized void startUpdateMetadataThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.remove());
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    private synchronized boolean isWait() {
        return wait;
    }

    private synchronized void setWait(boolean wait) {
        this.wait = wait;
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
    }

    @Override
    public void thumbnailsChanged() {
        stop = isWait();
        updateMetadata();
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (stop) {
            evt.stop();
            stop = false;
        } else {
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        setWait(false);
        if (updaters.size() > 0) {
            startUpdateMetadataThread();
        }
    }
}
