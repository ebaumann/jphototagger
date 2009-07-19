package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * An automatic task is a background task running as long as the next task
 * shall start.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-16
 */
public final class AutomaticTask {

    public static final AutomaticTask INSTANCE = new AutomaticTask();
    private Runnable runnable;

    private AutomaticTask() {
    }

    /**
     * Sets a new automatic task and calls {@link Thread#interrupt()} to the
     * currently running task if it's an instance of
     * <code>java.lang.Thread</code>.
     *
     * This means: The currently running task stops only when it is a thread
     * that will periodically check {@link Thread#isInterrupted()}.
     *
     * @param runnable
     */
    public synchronized void setTask(Runnable runnable) {
        this.runnable = runnable;
        shutdown();
        startTask(runnable);
    }

    /**
     * Interrupts the currently running tasks. For limitations see remarks:
     * {@link #setTask(java.lang.Runnable)}.
     */
    public void shutdown() {
        if (runnable != null) {
            if (runnable instanceof Thread) {
                ((Thread) runnable).interrupt();
            } else {
                AppLog.logWarning(AutomaticTask.class,
                        Bundle.getString("AutomaticTask.Error.Terminate", // NOI18N
                        runnable));
            }
        }
    }

    private void startTask(final Runnable runnable) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                runnable.run();
            }
        });
        t.setName(getName(runnable));
        t.start();
    }

    private String getName(Runnable runnable) {
        if (runnable instanceof Thread) {
            return ((Thread) runnable).getName();
        }
        return "Automatic task @ " + getClass().getName(); // NOI18N
    }
}
