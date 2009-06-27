package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.tasks.RecordsWithNotExistingFilesDeleter;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.event.listener.TaskListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.Task;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerRecordsWithNotExistingFilesDeleter
        implements ProgressListener, Task {

    private final JProgressBar progressBar;
    private final List<TaskListener> taskListeners =
            new ArrayList<TaskListener>();
    private volatile boolean stop = false;

    /**
     * Konstruktor.
     * 
     * @param progressBar  Progressbar zum Darstellen des Fortschritts oder null,
     *                     falls dieser nicht dargestellt werden soll
     */
    public ControllerRecordsWithNotExistingFilesDeleter(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Fügt einen Task-Beobachter hinzu.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (progressBar != null) {
            setProgressBar();
        }
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
            progressBar.setValue(progressBar.getMaximum());
        }
        notifyTaskListenerCompleted();
    }

    private synchronized void notifyTaskListenerCompleted() {
        for (TaskListener taskListener : taskListeners) {
            taskListener.taskCompleted();
        }
    }

    private void setProgressBar() {
        progressBar.setToolTipText(
                Bundle.getString(
                "ControllerRecordsWithNotExistingFilesDeleter.ProgressBarTooltipText.DeleteRecordsWithNotExistingFiles"));
        progressBar.setIndeterminate(true);
    }

    private void startThread() {
        RecordsWithNotExistingFilesDeleter deleter =
                new RecordsWithNotExistingFilesDeleter();
        deleter.addProgressListener(this);
        Thread thread = new Thread(deleter);
        thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
        thread.setName("Deleting records with not existing files" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    @Override
    public void start() {
        startThread();
    }

    @Override
    public void stop() {
        stop = true;
    }
}
