package de.elmar_baumann.lib.concurrent;

import de.elmar_baumann.lib.resource.Bundle;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

// Code from java.util.concurrent.Executor javadoc. Added shutdown()
/**
 * Executes runnables serial: The next runnable will be executed when the
 * previous has finished.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/17
 */
public final class SerialExecutor implements Executor {

    private final Queue<Runnable> runnables = new ArrayDeque<Runnable>();
    private final Executor executor;
    private Runnable active;

    public SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Empties the queue and interrupts the current active runnable.
     *
     * To interrupt it, the runnable has to be a {@link Thread} and periodically
     * calling {@link Thread#isInterrupted()}.
     */
    public synchronized void shutdown() {
        runnables.clear();
        if (active instanceof Thread) {
            ((Thread) active).interrupt();
        } else if (active != null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    Bundle.getString(
                    "SerialExecutor.Error.Shutdown.RunnableIsNotAThread", active)); // NOI18N
        }
    }

    /**
     * Returns the count of runnables.
     *
     * @return count of runnables
     */
    public synchronized int getCount() {
        int activeCount = active == null
                          ? 0
                          : 1;
        return activeCount + runnables.size();
    }

    @Override
    public synchronized void execute(final Runnable r) {
        runnables.offer(new Runnable() {

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
        if ((active = runnables.poll()) != null) {
            executor.execute(active);
        }
    }
}
