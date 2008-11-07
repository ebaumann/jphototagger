package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * Führt geplante Tasks aus.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class ControllerArrayScheduledTasks extends Controller
    implements ActionListener, Runnable, TaskListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JProgressBar progressBar = appPanel.getProgressBarScheduledTasks();
    private JButton buttonStop = appPanel.getButtonStopScheduledTasks();
    private Queue<Controller> controllers = new ConcurrentLinkedQueue<Controller>();
    private Controller activeController;
    private int milliSecondsToStart = UserSettings.getInstance().
        getMinutesToStartScheduledTasks() * 60 * 1000;

    public ControllerArrayScheduledTasks() {
        buttonStop.addActionListener(this);
        initArray();
    }

    @Override
    public void setControl(boolean control) {
        super.setControl(control);
        setControlToArray(control);
    }

    // Can't call if true, because some controllers will start after call.
    // The will be started in startFirstController() and taskCompleted()
    // if this controller has control
    private void setControlToArray(boolean control) {
        if (!control) {
            for (Controller controller : controllers) {
                controller.setControl(false);
            }
        }
    }

    private void handleButtonStopClicked() {
        buttonStop.setEnabled(false);
        setControl(false);
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

    synchronized private void startFirstController() {
        if (!controllers.isEmpty()) {
            buttonStop.setEnabled(true);
            activeController = controllers.remove();
            activeController.setControl(true);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(milliSecondsToStart);
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerArrayScheduledTasks.class.getName()).log(Level.WARNING, null, ex);
        }
        if (isControl()) {
            startFirstController();
        }
    }

    @Override
    synchronized public void taskCompleted() {
        activeController = null;
        System.gc();
        if (!controllers.isEmpty()) {
            activeController = controllers.remove();
            activeController.setControl(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonStop) {
            handleButtonStopClicked();
        }
    }
}
