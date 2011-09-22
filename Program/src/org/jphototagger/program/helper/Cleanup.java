package org.jphototagger.program.helper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.ReplaceableTask;
import org.jphototagger.api.concurrent.SerialTaskExecutor;
import org.jphototagger.program.tasks.ScheduledTasks;

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
        SerialTaskExecutor serialTaskExecutor = Lookup.getDefault().lookup(SerialTaskExecutor.class);
        ReplaceableTask replaceableTask = Lookup.getDefault().lookup(ReplaceableTask.class);

        ScheduledTasks.INSTANCE.cancelCurrentTasks();
        replaceableTask.cancelRunningTask();
        serialTaskExecutor.cancelAllTasks();

        boolean serialTasksRunning = serialTaskExecutor.getTaskCount() > 0;
        boolean sleep = (ScheduledTasks.INSTANCE.getCount() > 0) || serialTasksRunning;

        if (sleep) {
            sleep();
        }
    }

    private static void sleep() {
        try {

            // Let the tasks a little bit time to complete until they can interrupt
            Thread.sleep(MILLISECONDS_SLEEP);
        } catch (Exception ex) {
            Logger.getLogger(Cleanup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Cleanup() {
    }
}
