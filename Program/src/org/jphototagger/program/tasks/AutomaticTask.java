package org.jphototagger.program.tasks;


import org.jphototagger.lib.concurrent.Cancelable;

/**
 * An automatic task is a background task running as long as the next task
 * shall start.
 *
 * @author Elmar Baumann
 */
public final class AutomaticTask {
    public static final AutomaticTask INSTANCE = new AutomaticTask();
    private Runnable                  runnable;

    /**
     * Sets a new automatic task and calls {@link #cancelCurrentTask()} to the
     * currently running task.
     *
     * @param runnable runnable
     */
    public synchronized void setTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }

        cancelCurrentTask();
        this.runnable = runnable;
        startTask(runnable);
    }

    /**
     * Cancels the current task.
     * <p>
     * If the active runnable implements {@link Cancelable}, its method
     * {@link Cancelable#cancel()} will be called. If it does not implement
     * that interface and it is an instance of {@link Thread},
     * {@link Thread#interrupt()} will be called.
     */
    public void cancelCurrentTask() {
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

    private void startTask(final Runnable runnable) {
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

        return "JPhotoTagger: Automatic task @ " 
                + runnable.getClass().getSimpleName();
    }

    private AutomaticTask() {}
}
