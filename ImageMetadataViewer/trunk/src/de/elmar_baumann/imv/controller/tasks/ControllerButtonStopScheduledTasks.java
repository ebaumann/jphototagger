package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.ScheduledTasks;
import de.elmar_baumann.imv.view.panels.AppPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Listens to the {@link AppPanel#getButtonStopScheduledTasks()} and when it
 * performs action this class calls {@link ScheduledTasks#shutdown()} and
 * disables the stop button.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class ControllerButtonStopScheduledTasks implements ActionListener {

    JButton buttonStopTasks =
            GUI.INSTANCE.getAppPanel().getButtonStopScheduledTasks();

    public ControllerButtonStopScheduledTasks() {
        listen();
    }

    private void listen() {
        buttonStopTasks.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ScheduledTasks.INSTANCE.shutdown();
        buttonStopTasks.setEnabled(false);
    }
}
