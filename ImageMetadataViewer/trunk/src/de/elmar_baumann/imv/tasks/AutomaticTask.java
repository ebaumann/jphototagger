package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import java.lang.reflect.Method;

/**
 * An automatic task is a background task running as long as the next task
 * shall start.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-16
 */
public final class AutomaticTask {

    public static final AutomaticTask INSTANCE = new AutomaticTask();
    private static final String ALT_METHOD_NAME_INTERRUPT = "cancel"; // NOI18N
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
     * If the active has a method named <strong>cancel</strong> with no
     * parameters, it will be invoked instead of <strong>interrupt</strong>.
     *
     * @param runnable runnable
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
            interrupt(runnable);
        }
    }

    private synchronized void interrupt(Runnable r) {
        if (r == null) return;
        Method methodCancel = null;
        if (hasCancelMethod(r)) {
            try {
                methodCancel = r.getClass().getMethod(ALT_METHOD_NAME_INTERRUPT);
                methodCancel.invoke(r);
            } catch (Exception ex) {
                AppLog.logSevere(AutomaticTask.class, ex);
            }
        }
        if (methodCancel == null && r instanceof Thread) {
            ((Thread) r).interrupt();
        }
    }

    private boolean hasCancelMethod(Runnable runnable) {
        Method[] methods = runnable.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(ALT_METHOD_NAME_INTERRUPT) &&
                    method.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
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
