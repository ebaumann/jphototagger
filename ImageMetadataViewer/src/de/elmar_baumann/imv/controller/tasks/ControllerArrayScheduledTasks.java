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
        buttonStop.setEnabled(false);
        listenToActionSource();
        initArray();
    }

    private void listenToActionSource() {
        buttonStop.addActionListener(this);
    }

    @Override
    public void setControl(boolean control) {
        super.setControl(control);
        if (!control) {
            stopAllControllers();
        }
    }

    private void initArray() {
        ControllerAutoUpdateMetadataTask controllerPriority1 =
            new ControllerAutoUpdateMetadataTask(progressBar);
        controllerPriority1.addTaskListener(this);

        controllers.add(controllerPriority1);

        if (UserSettings.getInstance().isTaskRemoveRecordsWithNotExistingFiles()) {
            ControllerRecordsWithNotExistingFilesDeleter controllerPriority2 =
                new ControllerRecordsWithNotExistingFilesDeleter(progressBar);
            controllerPriority2.addTaskListener(this);
            controllers.add(controllerPriority2);
        }
    }

    private void startFirstController() {
        activeController = controllers.remove();
        activeController.setControl(true);
        buttonStop.setEnabled(true);
    }

    private void stopAllControllers() {
        // Spätere Benutung offenhalten (kein pop)
        if (activeController != null) {
            activeController.setControl(true);
        }
        for (Controller controller : controllers) {
            controller.setControl(false);
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
        activeController = null;
        System.gc();
        if (!controllers.isEmpty()) {
            activeController = controllers.remove();
            activeController.setControl(true);
        } else {
            buttonStop.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonStop) {
            buttonStop.setEnabled(false);
            setControl(false);
        }
    }
}
