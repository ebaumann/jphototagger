/*
 * @(#)FileSystem.java    Created on 2008-10-20
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.io;

import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.event.listener.impl.FileSystemListenerSupport;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;

import java.io.File;

/**
 * Base class for file system actions. Provides registering and notifying
 * listeners.
 *
 * @author Elmar Baumann
 */
public class FileSystem {
    private final FileSystemListenerSupport fsListenerSupport =
        new FileSystemListenerSupport();
    private final ProgressListenerSupport pListenerSupport =
        new ProgressListenerSupport();

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

    protected void notifyFileSystemListenersRenamed(File fromFile,
            File toFile) {
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
