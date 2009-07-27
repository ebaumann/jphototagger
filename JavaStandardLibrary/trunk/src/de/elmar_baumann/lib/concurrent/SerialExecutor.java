package de.elmar_baumann.lib.concurrent;

import java.lang.reflect.Method;
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
 * @version 2009-07-17
 */
public final class SerialExecutor implements Executor {

    private final Queue<Runnable> runnables = new ArrayDeque<Runnable>();
    private static final String ALT_METHOD_NAME_INTERRUPT = "cancel";
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
     *
     * If the active runnable has a method named <strong>cancel</strong> with
     * no parameters, it will be invoked instead of <strong>interrupt</strong>.
     */
    public synchronized void shutdown() {
        runnables.clear();
        interruptActive(active);
    }

    private synchronized void interruptActive(Runnable active) {
        if (active == null) return;
        Method methodCancel = null;
        if (hasCancelMethod(active)) {
            try {
                methodCancel = active.getClass().getMethod(
                        ALT_METHOD_NAME_INTERRUPT);
                methodCancel.invoke(active);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", ex); // NOI18N
            }
        }
        if (methodCancel == null && active instanceof Thread) {
            ((Thread) active).interrupt();
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
