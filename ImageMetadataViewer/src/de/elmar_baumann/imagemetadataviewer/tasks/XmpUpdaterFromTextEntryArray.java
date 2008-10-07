package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.data.TextEntry;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.resource.ProgressBarCurrentTasks;
import java.util.List;
import java.util.Stack;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class XmpUpdaterFromTextEntryArray implements ProgressListener {

    private Stack<XmpUpdaterFromTextEntry> updaters = new Stack<XmpUpdaterFromTextEntry>();
    private ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private JProgressBar progressBar;
    private boolean wait = false;
    private boolean stop = false;

    /**
     * Fügt zu aktualisierende Einträge hinzu.
     * 
     * @param filenames     Zu aktualisierende Dateien
     * @param textEntries   In alle Dateien zu schreibende Einträge
     * @param deleteEmpty   true, wenn in einer existierenden XMP-Datei
     *                      Einträge gelöscht werden sollen, wenn das
     *                      zugehörige Textfeld leer ist
     * @param append        true, wenn existierende Einträge um nicht
     *                      existierende ergänzt werden sollen und nicht
     *                      gelöscht
     */
    public void add(List<String> filenames, List<TextEntry> textEntries,
        boolean deleteEmpty, boolean append) {
        XmpUpdaterFromTextEntry updater = new XmpUpdaterFromTextEntry(filenames, textEntries, deleteEmpty, append);
        updater.addProgressListener(this);
        updaters.add(updater);
        startThread();
    }

    private void startThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.pop());
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    /**
     * Unterbricht alle Arbeiten.
     */
    public void stop() {
        stop = true;
    }

    private boolean isStop() {
        return stop;
    }

    synchronized private boolean isWait() {
        return wait;
    }

    synchronized private void setWait(boolean wait) {
        this.wait = wait;
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getRessource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            if (evt.getMaximum() > 0) {
                progressBar.setMaximum(evt.getMaximum());
            }
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (isStop()) {
            updaters.removeAllElements();
            evt.setStop(true);
        } else if (progressBar != null) {
            String filename = evt.getInfo().toString();
            progressBar.setValue(evt.getValue());
            progressBar.setToolTipText(filename);
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
            startThread();
        }
    }
}
