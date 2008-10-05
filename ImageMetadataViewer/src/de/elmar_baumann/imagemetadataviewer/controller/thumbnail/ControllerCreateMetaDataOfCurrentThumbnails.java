package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelAction;
import de.elmar_baumann.imagemetadataviewer.event.ThumbnailsPanelListener;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import java.util.List;
import java.util.Stack;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class ControllerCreateMetaDataOfCurrentThumbnails extends Controller
    implements ThumbnailsPanelListener, ProgressListener {

    private Stack<ImageMetadataToDatabase> updaters = new Stack<ImageMetadataToDatabase>();
    private boolean wait = false;
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private JProgressBar progressBar = appPanel.getProgressBarCreateMetaDataOfCurrentThumbnails();
    private boolean stopCurrent = false;

    public ControllerCreateMetaDataOfCurrentThumbnails() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
    }

    synchronized private void updateMetadata() {
        updaters.push(createUpdater(thumbnailsPanel.getFilenames()));
        startUpdateMetadataThread();
    }

    private ImageMetadataToDatabase createUpdater(List<String> files) {
        ImageMetadataToDatabase updater =
            new ImageMetadataToDatabase(files,
            UserSettings.getInstance().getMaxThumbnailLength());
        updater.setCreateThumbnails(true);
        updater.addProgressListener(this);
        updater.setForceUpdate(false);
        return updater;
    }

    private synchronized void startUpdateMetadataThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.pop());
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
    public void thumbnailSelected(ThumbnailsPanelAction action) {
        // Nichts tun
    }

    @Override
    public void allThumbnailsDeselected(ThumbnailsPanelAction action) {
        // Nichts tun
    }

    @Override
    public void thumbnailCountChanged() {
        if (isStarted()) {
            setStopCurrent(isWait());
            updateMetadata();
        }
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
