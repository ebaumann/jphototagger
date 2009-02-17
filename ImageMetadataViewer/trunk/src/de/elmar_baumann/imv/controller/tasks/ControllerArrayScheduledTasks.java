package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.Task;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * Führt geplante Tasks aus.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class ControllerArrayScheduledTasks
        implements ActionListener, Runnable, TaskListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JProgressBar progressBar = appPanel.getProgressBarScheduledTasks();
    private final JButton buttonStop = appPanel.getButtonStopScheduledTasks();
    private final Queue<Task> controllers = new ConcurrentLinkedQueue<Task>();
    private Task activeController;
    private int milliSecondsToStart = UserSettings.getInstance().getMinutesToStartScheduledTasks() * 60 * 1000;

    public ControllerArrayScheduledTasks() {
        listen();
        initArray();
    }

    private void handleButtonStopClicked() {
        buttonStop.setEnabled(false);
        stopController();
    }

    private void initArray() {
        ControllerAutoUpdateMetadataTask controllerAutoUpdateMetadataTask =
                new ControllerAutoUpdateMetadataTask(progressBar);

        controllerAutoUpdateMetadataTask.addTaskListener(this);

        controllers.add(controllerAutoUpdateMetadataTask);

        if (UserSettings.getInstance().isTaskRemoveRecordsWithNotExistingFiles()) {
            ControllerRecordsWithNotExistingFilesDeleter controllerRecordsWithNotExistingFilesDeleter =
                    new ControllerRecordsWithNotExistingFilesDeleter(progressBar);

            controllerRecordsWithNotExistingFilesDeleter.addTaskListener(this);

            controllers.add(controllerRecordsWithNotExistingFilesDeleter);
        }
    }

    private void listen() {
        buttonStop.addActionListener(this);
    }

    synchronized private void startFirstController() {
        if (!controllers.isEmpty()) {
            buttonStop.setEnabled(true);
            activeController = controllers.remove();
            activeController.start();
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(milliSecondsToStart);
        } catch (InterruptedException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
        }
        startFirstController();
    }

    @Override
    synchronized public void taskCompleted() {
        activeController = null;
        System.gc();
        if (!controllers.isEmpty()) {
            activeController = controllers.remove();
            activeController.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonStop) {
            handleButtonStopClicked();
        }
    }

    synchronized private void stopController() {
        for (Task task : controllers) {
            task.stop();
        }
    }
}
