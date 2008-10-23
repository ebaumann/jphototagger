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

    private List<FileSystemActionListener> actionListeners = new ArrayList<FileSystemActionListener>();
    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    protected void notifyProgressListenerStarted(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    protected void notifyProgressListenerPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    protected void notifyProgressListenerEnded(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    public void addActionListener(FileSystemActionListener listener) {
        actionListeners.add(listener);
    }

    protected void notifyActionListenersPerformed(
        FileSystemAction action, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionPerformed(action, src, target);
        }
    }

    protected void notifyActionListenersFailed(
        FileSystemAction action, FileSystemError error, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionFailed(action, error, src, target);
        }
    }
}
