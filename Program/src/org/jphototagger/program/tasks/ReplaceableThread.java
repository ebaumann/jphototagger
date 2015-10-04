package org.jphototagger.program.tasks;

import org.jphototagger.api.concurrent.Cancelable;

/**
 * Runs a background thread as long as the next Thread start.
 *
 * @author Elmar Baumann
 */
public final class ReplaceableThread {

    private Runnable runnable;

    /**
     * Cancels a currently running thread and starts a new one.
     *
     * @param runnable runnable invoked in a new thread.
     *                 Should implement {@link Cancelable}.
     */
    public synchronized void setTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }
        cancelCurrentThread();
        this.runnable = runnable;
        startThread(runnable);
    }

    /**
     * Cancels the currently running thread.
     *
     * <p>If the active runnable implements {@link Cancelable}, it's method
     * {@link Cancelable#cancel() } will be called. If it does not implement
     * that interface and if it's an instance of {@code Thread},
     * {@code Thread#interrupt()} will be called.
     */
    public synchronized void cancelCurrentThread() {
        if (runnable != null) {
            cancel(runnable);
        }
    }

    private synchronized void cancel(Runnable r) {
        if (r == null) {
            return;
        }
        if (r instanceof Cancelable) {
            ((Cancelable) r).cancel();
        } else if (r instanceof Thread) {
            ((Thread) r).interrupt();
        }
    }

    private void startThread(final Runnable runnable) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, getName(runnable));

        t.start();
    }

    private String getName(Runnable runnable) {
        if (runnable instanceof Thread) {
            return ((Thread) runnable).getName();
        }
        return "JPhotoTagger: ReplaceableThread @ " + runnable.getClass().getSimpleName();
    }
}
