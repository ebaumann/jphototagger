package org.jphototagger.lib.awt;

import java.awt.EventQueue;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class EventQueueUtil {

    /**
     * If the calling thread is the current AWT {@code EventQueue}'s dispatch thread, 
     * the {@code Runnable}'s {@code run()} will be called, else the {@code Runnable}
     * will be invoked through {@link EventQueue#invokeLater(java.lang.Runnable)}.
     * 
     * @param runnable 
     */
    public static void invokeInDispatchThread(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }
        
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            EventQueue.invokeLater(runnable);
        }
    }

    private EventQueueUtil() {
    }
}
