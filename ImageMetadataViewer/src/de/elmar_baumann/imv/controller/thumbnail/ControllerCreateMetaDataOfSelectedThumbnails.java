package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.resource.ProgressBarCurrentTasks;
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 * 
 * <em>Nur eine Instanz erzeugen!</em>
 * 
 * Der Aufruf von {@link #stop()} beendet alle noch wartenden Threads.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerCreateMetaDataOfSelectedThumbnails extends Controller
    implements ActionListener, ProgressListener {

    private Queue<ImageMetadataToDatabase> updaters = new ConcurrentLinkedQueue<ImageMetadataToDatabase>();
    private boolean wait = false;
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private JProgressBar progressBar;

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetaDataOfSelectedThumbnails() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        popup.addActionListenerUpdateXmp(this);
        popup.addActionListenerUpdateThumbnail(this);
        popup.addActionListenerUpdateAllMetadata(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && thumbnailsPanel.getSelectionCount() > 0) {
            updateMetadata(popup.getDatabaseUpdateOf(e.getSource()));
        }
    }

    private void updateMetadata(DatabaseUpdate update) {
        updaters.add(
            createUpdater(FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()), update));
        startUpdateMetadataThread();
    }

    synchronized private boolean isWait() {
        return wait;
    }

    synchronized private void setWait(boolean wait) {
        this.wait = wait;
    }

    private synchronized void startUpdateMetadataThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.remove());
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    private ImageMetadataToDatabase createUpdater(List<String> files, DatabaseUpdate update) {
        ImageMetadataToDatabase updater =
            new ImageMetadataToDatabase(files, update);
        updater.addProgressListener(this);
        return updater;
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getRessource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (isControl()) {
            if (progressBar != null) {
                progressBar.setValue(evt.getValue());
            }
        } else {
            updaters.clear();
            evt.setStop(true);
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar.setToolTipText(AppSettings.tooltipTextProgressBarCurrentTasks);
            progressBarProvider.releaseResource(this);
            progressBar = null;
        }
        setWait(false);
        if (updaters.size() > 0) {
            startUpdateMetadataThread();
        }
    }
}
