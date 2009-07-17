package de.elmar_baumann.imv.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shuts down all Tasks. Should be called when the application exits.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public final class Cleanup {

    /**
     * Wait time in milliseconds before giving control to the caller, so that
     * the threads can complete their current action before they check for
     * interruption.
     */
    private static long MILLISECONDS_WAIT = 2000;

    /**
     * Shuts down all Tasks.
     */
    public static void shutdown() {
        ScheduledTasks.INSTANCE.shutdown();
        AutomaticTask.INSTANCE.shutdown();
        UserTasksQueue.INSTANCE.shutdown();
        try {
            // Let the tasks a little bit time to complete until they can interrupt
            Thread.sleep(MILLISECONDS_WAIT);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Cleanup() {
    }
}
