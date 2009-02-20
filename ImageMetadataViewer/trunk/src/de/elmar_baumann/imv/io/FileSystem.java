package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.event.FileSystemAction;
import de.elmar_baumann.imv.event.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for file system actions. Provides registering and notifying listeners.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/20
 */
public class FileSystem {

    private final List<FileSystemActionListener> actionListeners = new ArrayList<FileSystemActionListener>();
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

    protected FileSystem() {}

    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    protected synchronized void notifyProgressListenerStarted(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    protected synchronized void notifyProgressListenerPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    protected synchronized void notifyProgressListenerEnded(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    public synchronized void addActionListener(FileSystemActionListener listener) {
        actionListeners.add(listener);
    }

    protected synchronized void notifyActionListenersPerformed(
        FileSystemAction action, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionPerformed(action, src, target);
        }
    }

    protected synchronized void notifyActionListenersFailed(
        FileSystemAction action, FileSystemError error, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionFailed(action, error, src, target);
        }
    }
}
