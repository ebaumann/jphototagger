package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.tasks.RecordsWithNotExistingFilesDeleter;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerRecordsWithNotExistingFilesDeleter extends Controller
    implements ProgressListener {

    private JProgressBar progressBar;
    private List<TaskListener> taskListeners = new ArrayList<TaskListener>();

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
    public void addTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    /**
     * Entfernt einen Task-Beobachter.
     * 
     * @param listener  Beobachter
     */
    public void removeTaskListener(TaskListener listener) {
        taskListeners.remove(listener);
    }

    @Override
    public void start() {
        super.start();
        RecordsWithNotExistingFilesDeleter deleter =
            new RecordsWithNotExistingFilesDeleter();
        deleter.addProgressListener(this);
        Thread thread = new Thread(deleter);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setToolTipText(
                Bundle.getString("ControllerRecordsWithNotExistingFilesDeleter.ProgressBarTooltipText.DeleteRecordsWithNotExistingFiles"));
            progressBar.setIndeterminate(true);
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        evt.setStop(isStopped());
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
            progressBar.setValue(progressBar.getMaximum());
        }
        notifyTaskListener();
    }

    private void notifyTaskListener() {
        for (TaskListener taskListener : taskListeners) {
            taskListener.taskCompleted();
        }
    }
}
