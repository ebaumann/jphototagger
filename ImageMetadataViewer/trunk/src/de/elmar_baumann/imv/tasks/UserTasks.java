package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.lib.concurrent.SerialExecutor;
import java.util.concurrent.Executors;

/**
 * Queues user tasks and starts them as thread after the previous user task has
 * finished.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public final class UserTasks {

    public static final UserTasks INSTANCE = new UserTasks();
    private final SerialExecutor executor =
            new SerialExecutor(Executors.newCachedThreadPool());

    /**
     * Adds a new user task.
     *
     * @param runnable runnable
     */
    public void add(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Returns the count of user tasks.
     *
     * @return count of user tasks
     */
    public int getCount() {
        return executor.getCount();
    }

    /**
     * Removes all added user tasks and calls {@link Thread#interrupt()} of the
     * currently running runnable if it's an instance of
     * <code>java.lang.Thread</code>.
     *
     * Thus means: The currently running task stops only when it is a thread
     * that will periodically check {@link Thread#isInterrupted()}.
     */
    public void shutdown() {
        executor.shutdown();
    }

    private UserTasks() {
    }
}
