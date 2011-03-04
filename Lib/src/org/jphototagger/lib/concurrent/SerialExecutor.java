package org.jphototagger.lib.concurrent;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.Queue;

//Code from java.util.concurrent.Executor javadoc. Added cancel()

/**
 * Executes runnables serial: The next runnable will be executed when the
 * previous has finished.
 *
 * @author Elmar Baumann
 */
public final class SerialExecutor implements Executor {
    private final Queue<Exec> runnables = new ArrayDeque<Exec>();
    private final Executor executor;
    private Exec active;

    public SerialExecutor(Executor executor) {
        if (executor == null) {
            throw new NullPointerException("executor == null");
        }

        this.executor = executor;
    }

    /**
     * Empties the queue and interrupts the current active runnable.
     *
     * If the active runnable implements {@link Cancelable}, its method
     * {@link Cancelable#cancel()} will be called. If it does not implement
     * that interface and it is an instance of {@link Thread},
     * {@link Thread#interrupt()} will be called.
     */
    public synchronized void cancel() {
        runnables.clear();
        cancel(active);
    }

    private synchronized void cancel(Exec active) {
        if (active == null) {
            return;
        }

        if (active.r instanceof Cancelable) {
            ((Cancelable) active.r).cancel();
        } else if (active.r instanceof Thread) {
            ((Thread) active.r).interrupt();
        }
    }

    /**
     * Returns the count of runnables.
     *
     * @return count of runnables
     */
    public synchronized int getCount() {
        int activeCount = (active == null)
                          ? 0
                          : 1;

        return activeCount + runnables.size();
    }

    @Override
    public synchronized void execute(final Runnable r) {
        runnables.offer(new Exec(r));

        if (active == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((active = runnables.poll()) != null) {
            executor.execute(active);
        }
    }

    private class Exec implements Runnable {
        final Runnable r;

        Exec(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } finally {
                scheduleNext();
            }
        }
    }
}
