package de.elmar_baumann.imagemetadataviewer.controller.tasks;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.event.TaskListener;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
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
    private Stack<Controller> controllers = new Stack<Controller>();
    private Controller runningController;
    private int milliSecondsToStart = UserSettings.getInstance().
        getMinutesToStartScheduledTasks() * 60 * 1000;

    public ControllerArrayScheduledTasks() {
        buttonStop.setEnabled(false);
        listenToActionSource();
        initArray();
    }

    private void listenToActionSource() {
        buttonStop.addActionListener(this);
    }

    @Override
    public void stop() {
        stopAllControllers();
        super.stop();
    }

    private void initArray() {
        ControllerAutoUpdateMetadataTask controllerPriority1 =
            new ControllerAutoUpdateMetadataTask(progressBar);
        controllerPriority1.addTaskListener(this);

        controllers.push(controllerPriority1);

        if (UserSettings.getInstance().isTaskRemoveRecordsWithNotExistingFiles()) {
            ControllerRecordsWithNotExistingFilesDeleter controllerPriority2 =
                new ControllerRecordsWithNotExistingFilesDeleter(progressBar);
            controllerPriority2.addTaskListener(this);
            controllers.push(controllerPriority2);
        }
    }

    private void startFirstController() {
        runningController = controllers.pop();
        runningController.start();
        buttonStop.setEnabled(true);
    }

    private void stopAllControllers() {
        // Spätere Benutung offenhalten (kein pop)
        if (runningController != null) {
            runningController.stop();
        }
        for (Controller controller : controllers) {
            controller.stop();
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(milliSecondsToStart);
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerArrayScheduledTasks.class.getName()).log(Level.SEVERE, null, ex);
        }
        startFirstController();
    }

    @Override
    public void taskCompleted() {
        runningController = null;
        System.gc();
        if (!controllers.isEmpty()) {
            runningController = controllers.pop();
            runningController.start();
        } else {
            buttonStop.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonStop) {
            buttonStop.setEnabled(false);
            stop();
        }
    }
}
