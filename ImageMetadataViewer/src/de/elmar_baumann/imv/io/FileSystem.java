/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.event.FileSystemEvent;
import de.elmar_baumann.imv.event.listener.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for file system actions. Provides registering and notifying listeners.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
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
        FileSystemEvent action, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionPerformed(action, src, target);
        }
    }

    protected synchronized void notifyActionListenersFailed(
        FileSystemEvent action, FileSystemError error, File src, File target) {
        
        for (FileSystemActionListener listener : actionListeners) {
            listener.actionFailed(action, error, src, target);
        }
    }
}
