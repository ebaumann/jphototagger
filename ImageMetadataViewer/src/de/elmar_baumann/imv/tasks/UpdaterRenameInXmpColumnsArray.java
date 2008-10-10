package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.ProgressBarCurrentTasks;
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
public class UpdaterRenameInXmpColumnsArray implements ProgressListener {

    private Queue<UpdaterRenameInXmpColumns> updaters = new ConcurrentLinkedQueue<UpdaterRenameInXmpColumns>();
    private boolean wait = false;
    private JProgressBar progressBar;
    private ProgressBarCurrentTasks progressBarProvider = ProgressBarCurrentTasks.getInstance();
    private boolean stop = false;

    synchronized private void setWait(boolean wait) {
        this.wait = wait;
    }
    
    synchronized private boolean isWait() {
        return wait;
    }

    /**
     * Beendet alle Threads.
     */
    synchronized public void stop() {
        updaters.clear();
        stop = true;
    }

    synchronized public void update(List<String> filenames, Column column,
        String oldValue, String newValue) {
        updaters.add(new UpdaterRenameInXmpColumns(filenames, column, oldValue, newValue));
        startNextThread();
    }

    synchronized private void startNextThread() {
        if (!stop && !isWait() && !updaters.isEmpty()) {
            setWait(true);
            UpdaterRenameInXmpColumns updater = updaters.remove();
            updater.addProgressListener(this);
            Thread thread = new Thread(updater);
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setStop(stop);
        progressBar = (JProgressBar) progressBarProvider.getRessource(this);
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        evt.setStop(stop);
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar = null;
            progressBarProvider.releaseResource(this);
        }
        setWait(false);
        startNextThread();
    }
}
