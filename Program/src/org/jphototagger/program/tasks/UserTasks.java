package org.jphototagger.program.tasks;

import org.jphototagger.lib.concurrent.SerialExecutor;

import java.util.concurrent.Executors;

/**
 * Queues user tasks and starts them as thread after the previous user task has
 * finished.
 *
 * @author Elmar Baumann
 */
public final class UserTasks {
    public static final UserTasks INSTANCE = new UserTasks();
    private final SerialExecutor  executor =
        new SerialExecutor(Executors.newCachedThreadPool());

    /**
     * Adds a new user task.
     *
     * @param runnable runnable
     */
    public void add(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }

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
     * Removes all added user tasks.
     * <p>
     * If the active runnable implements
     * {@link org.jphototagger.lib.concurrent.}, its method
     * {@link org.jphototagger.lib.concurrent.Cancelable#cancel()} will be
     * called. If it does not implement that interface and it is an instance of
     * {@link Thread}, {@link Thread#interrupt()} will be called.
     */
    public void cancelCurrentTasks() {
        executor.cancel();
    }

    private UserTasks() {}
}
