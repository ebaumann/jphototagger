package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ProgressBarCurrentTasks;
import de.elmar_baumann.imv.tasks.Task;
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
import javax.swing.SwingUtilities;

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

    private final Queue<InsertImageFilesIntoDatabase> updaters =
            new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private final PopupMenuPanelThumbnails popupMenu =
            PopupMenuPanelThumbnails.INSTANCE;
    private final ProgressBarCurrentTasks progressBarProvider =
            ProgressBarCurrentTasks.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();
    private JProgressBar progressBar;
    private volatile boolean wait = false;
    private volatile boolean stop = false;

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetadataOfSelectedThumbnails() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerUpdateThumbnail(this);
        popupMenu.addActionListenerUpdateMetadata(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getSelectionCount() > 0) {
            updateMetadata(popupMenu.getMetadataToInsertIntoDatabase(
                    e.getSource()));
        }
    }

    private void updateMetadata(
            EnumSet<InsertImageFilesIntoDatabase.Insert> what) {
        updaters.add(createUpdater(FileUtil.getAsFilenames(
                thumbnailsPanel.getSelectedFiles()), what));
        startUpdateMetadataThread();
    }

    private synchronized boolean isWait() {
        return wait;
    }

    private synchronized void setWait(boolean wait) {
        this.wait = wait;
    }

    private synchronized void startUpdateMetadataThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.remove());
            thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
            thread.setName("Creating metadata of selected thumbnails" + " @ " + // NOI18N
                    getClass().getName());
            thread.start();
        }
    }

    private InsertImageFilesIntoDatabase createUpdater(List<String> files,
            EnumSet<InsertImageFilesIntoDatabase.Insert> what) {

        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                files, what);
        updater.addProgressListener(this);
        return updater;
    }

    @Override
    public synchronized void progressStarted(final ProgressEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar = (JProgressBar) progressBarProvider.getResource(
                        this);
                if (progressBar != null) {
                    progressBar.setMinimum(evt.getMinimum());
                    progressBar.setMaximum(evt.getMaximum());
                    progressBar.setValue(evt.getValue());
                }
            }
        });
    }

    @Override
    public synchronized void progressPerformed(ProgressEvent evt) {
        if (stop) {
            updaters.clear();
            evt.stop();
            setWait(false);
        } else {
            if (progressBar != null) {
                progressBar.setValue(evt.getValue());
            }
        }
    }

    @Override
    public synchronized void progressEnded(final ProgressEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setValue(evt.getValue());
                    progressBar.setToolTipText(
                            AppTexts.tooltipTextProgressBarCurrentTasks);
                    progressBar = null;
                    progressBarProvider.releaseResource(this);
                }
                setWait(false);
                if (updaters.size() > 0) {
                    startUpdateMetadataThread();
                }
            }
        });
    }

    @Override
    public void start() {
        stop = false;
    }

    @Override
    public void stop() {
        stop = true;
    }
}
