/*
 * @(#)FileSystemListenerSupport.java    Created on 2008-10-18
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.FileSystemListener;

import java.io.File;

/**
 *
 * @author  Elmar Baumann
 */
public final class FileSystemListenerSupport
        extends ListenerSupport<FileSystemListener> {
    public void notifyCopied(File source, File target) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        synchronized (listeners) {
            for (FileSystemListener listener : listeners) {
                listener.fileCopied(source, target);
            }
        }
    }

    public void notifyMoved(File source, File target) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        synchronized (listeners) {
            for (FileSystemListener listener : listeners) {
                listener.fileMoved(source, target);
            }
        }
    }

    public void notifyDeleted(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        synchronized (listeners) {
            for (FileSystemListener listener : listeners) {
                listener.fileDeleted(file);
            }
        }
    }

    public void notifyRenamed(File fromFile, File toFile) {
        if (fromFile == null) {
            throw new NullPointerException("oldFile == null");
        }

        if (toFile == null) {
            throw new NullPointerException("newFile == null");
        }

        synchronized (listeners) {
            for (FileSystemListener listener : listeners) {
                listener.fileRenamed(fromFile, toFile);
            }
        }
    }
}
