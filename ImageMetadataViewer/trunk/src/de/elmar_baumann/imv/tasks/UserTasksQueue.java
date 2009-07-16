package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Queues user tasks and starts them as thread after the previous user task has
 * finished.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public final class UserTasksQueue {

    public static final UserTasksQueue INSTANCE = new UserTasksQueue();
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

    private UserTasksQueue() {
    }

    // Code from java.util.concurrent.Executor javadoc. Added shutdown()
    private class SerialExecutor implements Executor {

        private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
        private final Executor executor;
        private Runnable active;

        SerialExecutor(Executor executor) {
            this.executor = executor;
        }

        synchronized void shutdown() {
            tasks.clear();
            if (active instanceof Thread) {
                ((Thread) active).interrupt();
            } else if (active != null) {
                AppLog.logWarning(AutomaticTask.class, Bundle.getString(
                        "UserTasksQueue.Error.Terminate", active));
            }
        }

        @Override
        public synchronized void execute(final Runnable r) {
            tasks.offer(new Runnable() {

                @Override
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (active == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((active = tasks.poll()) != null) {
                executor.execute(active);
            }
        }
    }
}
