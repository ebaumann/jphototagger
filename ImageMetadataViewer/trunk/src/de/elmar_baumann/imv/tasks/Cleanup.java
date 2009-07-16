package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.resource.GUI;

/**
 * Cleans up when the application will exit.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public final class Cleanup implements AppExitListener {

    public Cleanup() {
        GUI.INSTANCE.getAppFrame().addAppExitListener(this);
    }

    @Override
    public void appWillExit() {
        AutomaticTask.INSTANCE.shutdown();
        UserTasksQueue.INSTANCE.shutdown();
    }
}
