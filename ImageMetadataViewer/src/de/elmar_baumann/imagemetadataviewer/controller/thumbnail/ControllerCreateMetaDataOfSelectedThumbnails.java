package de.elmar_baumann.imagemetadataviewer.controller.thumbnail;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.resource.ProgressBarCurrentTasks;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.ArrayList;
import javax.swing.JProgressBar;

/**
 * Kontrolliert die Aktion: Metadaten erzeugen für ausgewählte Bilder,
 * ausgelöst von {@link de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails}.
 * 
 * <em>Nur eine Instanz erzeugen!</em>
 * 
 * Der Aufruf von {@link #stop()} beendet alle noch wartenden Threads.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerCreateMetaDataOfSelectedThumbnails extends Controller
    implements ActionListener, ProgressListener {

    private Stack<ImageMetadataToDatabase> updaters = new Stack<ImageMetadataToDatabase>();
    private boolean wait = false;
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private JProgressBar progressBar;

    /**
     * Konstruktor. <em>Nur eine Instanz erzeugen!</em>
     */
    public ControllerCreateMetaDataOfSelectedThumbnails() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        popup.addActionListenerUpdateAllMetadata(this);
        popup.addActionListenerUpdateTextMetadata(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            updateMetadataOfSelectedImages(
                popup.isUpdateTextMetadata(e.getActionCommand()));
        }
    }

    private void updateMetadataOfSelectedImages(boolean onlyTextMetadata) {
        if (popup.getThumbnailsPanel().getSelectionCount() > 0) {
            updateMetadata(onlyTextMetadata);
        }
    }

    private void updateMetadata(boolean onlyTextMetadata) {
        updaters.push(
            createUpdater(popup.getThumbnailsPanel().getSelectedFilenames(), onlyTextMetadata));
        startUpdateMetadataThread();
    }

    private boolean isWait() {
        return wait;
    }

    private void setWait(boolean wait) {
        this.wait = wait;
    }

    private synchronized void startUpdateMetadataThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.pop());
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    private ImageMetadataToDatabase createUpdater(ArrayList<String> files,
        boolean onlyTextMetadata) {
        ImageMetadataToDatabase updater =
            new ImageMetadataToDatabase(files,
            UserSettings.getInstance().getMaxThumbnailLength());
        updater.setCreateThumbnails(!onlyTextMetadata);
        updater.addProgressListener(this);
        updater.setForceUpdate(true);
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
        if (isStarted()) {
            if (progressBar != null) {
                progressBar.setValue(evt.getValue());
            }
        } else {
            updaters.removeAllElements();
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
