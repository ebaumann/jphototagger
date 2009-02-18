package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.resource.ProgressBarCurrentTasks;
import de.elmar_baumann.imv.tasks.Task;
import de.elmar_baumann.imv.types.MetaDataForceDbUpdate;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
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
public final class ControllerCreateMetadataOfSelectedThumbnails
        implements ActionListener, ProgressListener, Task {

    private final Queue<InsertImageFilesIntoDatabase> updaters = new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private final ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private JProgressBar progressBar;
    volatile private boolean wait = false;
    volatile private boolean stop = false;

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        listen();
    }

    private void listen() {
        popup.addActionListenerUpdateThumbnail(this);
        popup.addActionListenerUpdateMetadata(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            updateMetadata(popup.getDatabaseUpdateOf(e.getSource()));
        }
    }

    private void updateMetadata(EnumSet<MetaDataForceDbUpdate> forceUpdateOf) {
        updaters.add(createUpdater(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()), forceUpdateOf));
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

    private InsertImageFilesIntoDatabase createUpdater(List<String> files, EnumSet<MetaDataForceDbUpdate> forceUpdateOf) {
        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(files, forceUpdateOf);
        updater.addProgressListener(this);
        return updater;
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (stop) {
            updaters.clear();
            evt.stop();
        } else {
            if (progressBar != null) {
                progressBar.setValue(evt.getValue());
            }
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar.setToolTipText(AppSettings.tooltipTextProgressBarCurrentTasks);
            progressBar = null;
            progressBarProvider.releaseResource(this);
        }
        setWait(false);
        if (updaters.size() > 0) {
            startUpdateMetadataThread();
        }
    }

    @Override
    public void start() {
        synchronized (this) {
            stop = false;
        }
    }

    @Override
    public void stop() {
        synchronized (this) {
            stop = true;
        }
    }
}
