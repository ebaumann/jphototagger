package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.event.listener.ProgressListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Adds, removes and notifies {@link ProgressListener} instances.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-18
 */
public final class ProgressListenerSupport {

    private final Set<ProgressListener> listeners = Collections.synchronizedSet(new HashSet<ProgressListener>());

    public void addProgressListener(ProgressListener listener) {
        listeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressStarted(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(ProgressEvent event) {
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressStarted(event);
            }
        }
    }
    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressPerformed(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@link ProgressEvent#isStop()}
     */
    public boolean notifyPerformed(ProgressEvent event) {
        boolean isStop = false;
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressPerformed(event);
            }
        }
        return isStop;
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressEnded(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(ProgressEvent event) {
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressEnded(event);
            }
        }
    }
}
