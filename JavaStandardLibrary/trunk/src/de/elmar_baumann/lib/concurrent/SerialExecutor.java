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

    private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
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
        tasks.clear();
        if (active instanceof Thread) {
            ((Thread) active).interrupt();
        } else if (active != null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    Bundle.getString(
                    "SerialExecutor.Error.Shutdown.RunnableIsNotAThread", active));
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
