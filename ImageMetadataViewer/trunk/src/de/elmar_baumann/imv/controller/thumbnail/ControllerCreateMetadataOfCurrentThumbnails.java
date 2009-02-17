package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
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

    private final Queue<ImageMetadataToDatabase> updaters = new ConcurrentLinkedQueue<ImageMetadataToDatabase>();
    private boolean wait = false;
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final JProgressBar progressBar = appPanel.getProgressBarCreateMetadataOfCurrentThumbnails();
    private boolean stopCurrent = false;

    public ControllerCreateMetadataOfCurrentThumbnails() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    synchronized private void updateMetadata() {
        updaters.add(createUpdater(FileUtil.getAsFilenames(thumbnailsPanel.getFiles())));
        startUpdateMetadataThread();
    }

    private ImageMetadataToDatabase createUpdater(List<String> files) {
        ImageMetadataToDatabase updater =
                new ImageMetadataToDatabase(files, DatabaseUpdate.IF_LAST_MODIFIED_CHANGED);
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

    synchronized private boolean isWait() {
        return wait;
    }

    synchronized private void setWait(boolean wait) {
        this.wait = wait;
    }

    synchronized private void setStopCurrent(boolean stop) {
        stopCurrent = stop;
    }

    synchronized private boolean isStopCurrent() {
        return stopCurrent;
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
    }

    @Override
    public void thumbnailsChanged() {
        setStopCurrent(isWait());
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
        if (isStopCurrent()) {
            evt.setStop(true);
            setStopCurrent(false);
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
