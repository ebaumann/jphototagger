package org.jphototagger.program.tasks;

import java.util.concurrent.Executors;
import org.jphototagger.lib.concurrent.SerialExecutor;

/**
 * Maybe considered:
 * ExecutorService exec = Executors.newFixedThreadPool(1);
 * for (int i = 0; i < count; i++) {
 *         exec.execute(new RunnableInstance());
 * }
 * exec.shutdown();
 */

/**
 * Queues user tasks and starts them as thread after the previous user task has
 * finished.
 *
 * @author Elmar Baumann
 */
final class JptSerialExecutor {

    static final JptSerialExecutor INSTANCE = new JptSerialExecutor();
    private final SerialExecutor executor = new SerialExecutor(Executors.newCachedThreadPool());

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
     * {@code org.jphototagger.lib.concurrent.}, its method
     * {@code org.jphototagger.lib.concurrent.Cancelable#cancel()} will be
     * called. If it does not implement that interface and it is an instance of
     * {@code Thread}, {@code Thread#interrupt()} will be called.
     */
    public void cancelCurrentTasks() {
        executor.cancel();
    }

    private JptSerialExecutor() {
    }
}
