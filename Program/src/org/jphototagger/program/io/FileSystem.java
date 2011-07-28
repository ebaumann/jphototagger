package org.jphototagger.program.io;

import org.jphototagger.lib.event.listener.FileSystemListener;
import org.jphototagger.domain.event.listener.impl.FileSystemListenerSupport;
import org.jphototagger.domain.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.lib.event.listener.ProgressListener;
import org.jphototagger.lib.event.ProgressEvent;
import java.io.File;

/**
 * Base class for file system actions. Provides registering and notifying
 * listeners.
 *
 * @author Elmar Baumann
 */
public class FileSystem {
    private final FileSystemListenerSupport fsListenerSupport = new FileSystemListenerSupport();
    private final ProgressListenerSupport pListenerSupport = new ProgressListenerSupport();

    protected FileSystem() {}

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

    public void addFileSystemListener(FileSystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        fsListenerSupport.add(listener);
    }

    protected void notifyFileSystemListenersCopied(File src, File target) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        fsListenerSupport.notifyCopied(src, target);
    }

    protected void notifyFileSystemListenersMoved(File src, File target) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        fsListenerSupport.notifyMoved(src, target);
    }

    protected void notifyFileSystemListenersRenamed(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("fromFile == null");
        }

        if (toFile == null) {
            throw new NullPointerException("toFile == null");
        }

        fsListenerSupport.notifyRenamed(fromFile, toFile);
    }

    protected void notifyFileSystemListenersDeleted(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        fsListenerSupport.notifyDeleted(file);
    }
}
