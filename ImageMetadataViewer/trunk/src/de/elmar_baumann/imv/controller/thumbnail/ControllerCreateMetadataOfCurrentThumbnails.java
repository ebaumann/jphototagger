package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ProgressBarCreateMetadataOfCurrentThumbnails;
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

    private final Queue<InsertImageFilesIntoDatabase> updaters =
            new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private volatile boolean wait = false;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();
    private final ProgressBarCreateMetadataOfCurrentThumbnails progressBarProvider =
            ProgressBarCreateMetadataOfCurrentThumbnails.INSTANCE;
    private JProgressBar progressBar;
    private boolean stop = false;

    public ControllerCreateMetadataOfCurrentThumbnails() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    private synchronized void updateMetadata() {
        updaters.add(createUpdater(FileUtil.getAsFilenames(thumbnailsPanel.
                getFiles())));
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
            thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
            thread.setName("Creating metadata of all thumbnails in panel" + // NOI18N
                    " @ " + getClass().getName()); // NOI18N
            thread.start();
        }
    }

    private boolean isWait() {
        return wait;
    }

    private void setWait(boolean wait) {
        this.wait = wait;
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
    }

    @Override
    public synchronized void thumbnailsChanged() {
        stop = isWait();
        updateMetadata();
    }

    private synchronized void setProgressBarValueAndTooltipText(
            ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            if (evt.getInfo() != null) {
                progressBar.setToolTipText(evt.getInfo().toString());
            }
        }
    }

    @Override
    public synchronized void progressStarted(ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
        }
        setProgressBarValueAndTooltipText(evt);
    }

    @Override
    public synchronized void progressPerformed(ProgressEvent evt) {
        if (stop) {
            evt.stop();
            stop = false;
            progressBar = null;
            progressBarProvider.releaseResource(this);
        } else {
            setProgressBarValueAndTooltipText(evt);
        }
    }

    @Override
    public synchronized void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar.setToolTipText(AppTexts.tooltipTextProgressBarDirectory);
        }
        progressBar = null;
        progressBarProvider.releaseResource(this);
        setWait(false);
        if (updaters.size() > 0) {
            startUpdateMetadataThread();
        }
    }
}
