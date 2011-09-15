package org.jphototagger.program.io;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;

/**
 * Base class for file system actions. Provides registering and notifying
 * listeners.
 *
 * @author Elmar Baumann
 */
public class FileSystem {

    private final ProgressListenerSupport pListenerSupport = new ProgressListenerSupport();

    protected FileSystem() {
    }

    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pListenerSupport.add(listener);
    }

    protected void notifyProgressListenerStarted(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        pListenerSupport.notifyStarted(evt);
    }

    protected void notifyProgressListenerPerformed(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        pListenerSupport.notifyPerformed(evt);
    }

    protected void notifyProgressListenerEnded(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        pListenerSupport.notifyEnded(evt);
    }
}
