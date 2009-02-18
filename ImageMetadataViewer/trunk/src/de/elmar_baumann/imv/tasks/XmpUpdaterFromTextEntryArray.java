package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.ProgressBarCurrentTasks;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class XmpUpdaterFromTextEntryArray implements ProgressListener {

    private final Queue<XmpUpdaterFromTextEntry> updaters = new ConcurrentLinkedQueue<XmpUpdaterFromTextEntry>();
    private final ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private JProgressBar progressBar;
    private boolean wait = false;
    volatile private boolean stop = false;

    /**
     * Fügt zu aktualisierende Einträge hinzu.
     * 
     * @param filenames     Zu aktualisierende Dateien
     * @param textEntries   In alle Dateien zu schreibende Einträge
     * @param writeOptions  Optionen
     */
    public void add(List<String> filenames, List<TextEntry> textEntries,
        EnumSet<XmpMetadata.UpdateOption> writeOptions) {
        XmpUpdaterFromTextEntry updater = new XmpUpdaterFromTextEntry(filenames, textEntries, writeOptions);
        updater.addProgressListener(this);
        updaters.add(updater);
        startThread();
    }

    private void startThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.remove());
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
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
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
            updaters.clear();
            evt.stop();
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
            progressBar = null;
            progressBarProvider.releaseResource(this);
        }
        setWait(false);
        if (updaters.size() > 0) {
            startThread();
        }
    }
}
