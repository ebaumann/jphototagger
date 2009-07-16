package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * Verwaltet Threadinstanzen der Klasse
 * {@link de.elmar_baumann.imv.tasks.XmpUpdaterRenameInColumns}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UpdaterRenameInXmpColumnsArray implements ProgressListener {

    private final Queue<UpdaterRenameInXmpColumns> updaters =
            new ConcurrentLinkedQueue<UpdaterRenameInXmpColumns>();
    private final ProgressBarUserTasks progressBarProvider =
            ProgressBarUserTasks.INSTANCE;
    private JProgressBar progressBar;
    private boolean wait = false;
    private boolean stop = false;

    private synchronized void setWait(boolean wait) {
        this.wait = wait;
    }

    private synchronized boolean isWait() {
        return wait;
    }

    /**
     * Beendet alle Threads.
     */
    public synchronized void stop() {
        updaters.clear();
        stop = true;
    }

    public synchronized void update(List<String> filenames, Column column,
            String oldValue, String newValue) {
        updaters.add(new UpdaterRenameInXmpColumns(filenames, column, oldValue,
                newValue));
        startNextThread();
    }

    private synchronized void startNextThread() {
        if (!stop && !isWait() && !updaters.isEmpty()) {
            setWait(true);
            UpdaterRenameInXmpColumns updater = updaters.remove();
            updater.addProgressListener(this);
            Thread thread = new Thread(updater);
            thread.setName("Renaming XMP in " + updater.getColumn().getName() + // NOI18N
                    " @ " + getClass().getName()); // NOI18N
            thread.start();
        }
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        checkStopEvent(evt);
        setProgressBarStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        checkStopEvent(evt);
        setProgressBarPerformed(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        setProgressBarEnded(evt);
        setWait(false);
        startNextThread();
    }

    private void setProgressBarStarted(final ProgressEvent evt) {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
    }

    private void setProgressBarPerformed(final ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
        }
    }

    private void setProgressBarEnded(final ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar = null;
            progressBarProvider.releaseResource(this);
        }
    }
}
