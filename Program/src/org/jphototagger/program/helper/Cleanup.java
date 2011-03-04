package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.tasks.AutomaticTask;
import org.jphototagger.program.tasks.ScheduledTasks;
import org.jphototagger.program.tasks.UserTasks;

/**
 * Shuts down all Tasks. Should be called when the application exits.
 *
 * @author Elmar Baumann
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
        ScheduledTasks.INSTANCE.cancelCurrentTasks();
        AutomaticTask.INSTANCE.cancelCurrentTask();
        UserTasks.INSTANCE.cancelCurrentTasks();

        boolean sleep = (ScheduledTasks.INSTANCE.getCount() > 0) || (UserTasks.INSTANCE.getCount() > 0);

        if (sleep) {
            sleep();
        }
    }

    private static void sleep() {
        try {

            // Let the tasks a little bit time to complete until they can interrupt
            Thread.sleep(MILLISECONDS_SLEEP);
        } catch (Exception ex) {
            AppLogger.logSevere(Cleanup.class, ex);
        }
    }

    private Cleanup() {}
}
