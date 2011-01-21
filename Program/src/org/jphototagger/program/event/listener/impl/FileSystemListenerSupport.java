package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.FileSystemListener;

import java.io.File;

/**
 *
 * @author Elmar Baumann
 */
public final class FileSystemListenerSupport
        extends ListenerSupport<FileSystemListener> {
    public void notifyCopied(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("source == null");
        }

        if (toFile == null) {
            throw new NullPointerException("target == null");
        }

        for (FileSystemListener listener : listeners) {
            listener.fileCopied(fromFile, toFile);
        }
    }

    public void notifyMoved(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("source == null");
        }

        if (toFile == null) {
            throw new NullPointerException("target == null");
        }

        for (FileSystemListener listener : listeners) {
            listener.fileMoved(fromFile, toFile);
        }
    }

    public void notifyDeleted(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        for (FileSystemListener listener : listeners) {
            listener.fileDeleted(file);
        }
    }

    public void notifyRenamed(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("oldFile == null");
        }

        if (toFile == null) {
            throw new NullPointerException("newFile == null");
        }

        for (FileSystemListener listener : listeners) {
            listener.fileRenamed(fromFile, toFile);
        }
    }
}
