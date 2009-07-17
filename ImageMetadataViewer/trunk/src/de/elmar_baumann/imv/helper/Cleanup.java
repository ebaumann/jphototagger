package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.tasks.AutomaticTask;
import de.elmar_baumann.imv.tasks.ScheduledTasks;
import de.elmar_baumann.imv.tasks.UserTasks;

/**
 * Shuts down all Tasks. Should be called when the application exits.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public final class Cleanup {

    /**
     * Sleep time in milliseconds before giving control to the caller, so that
     * the threads can complete their current action before they check for
     * interruption.
     */
    private static long MILLISECONDS_SLEEP = 2000;

    /**
     * Shuts down all Tasks.
     */
    public static void shutdown() {
        ScheduledTasks.INSTANCE.shutdown();
        AutomaticTask.INSTANCE.shutdown();
        UserTasks.INSTANCE.shutdown();
        boolean sleep = ScheduledTasks.INSTANCE.getCount() > 0 ||
                UserTasks.INSTANCE.getCount() > 0;
        if (sleep) {
            sleep();
        }
    }

    private static void sleep() {
        try {
            // Let the tasks a little bit time to complete until they can interrupt
            Thread.sleep(MILLISECONDS_SLEEP);
        } catch (InterruptedException ex) {
            AppLog.logSevere(Cleanup.class, ex);
        }
    }

    private Cleanup() {
    }
}
