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
package de.elmar_baumann.jpt.io;

import de.elmar_baumann.jpt.event.FileSystemEvent;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.FileSystemListenerSupport;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import java.io.File;

/**
 * Base class for file system actions. Provides registering and notifying listeners.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public class FileSystem {

    private final FileSystemListenerSupport fsListenerSupport = new FileSystemListenerSupport();
    private final ProgressListenerSupport   pListenerSupport  = new ProgressListenerSupport();

    protected FileSystem() {}

    public void addProgressListener(ProgressListener listener) {
        pListenerSupport.add(listener);
    }

    protected void notifyProgressListenerStarted(ProgressEvent evt) {
        pListenerSupport.notifyStarted(evt);
    }

    protected void notifyProgressListenerPerformed(ProgressEvent evt) {
        pListenerSupport.notifyPerformed(evt);
    }

    protected void notifyProgressListenerEnded(ProgressEvent evt) {
        pListenerSupport.notifyEnded(evt);
    }

    public void addFileSystemListener(FileSystemListener listener) {
        fsListenerSupport.add(listener);
    }

    protected void notifyFileSystemListenersPerformed(FileSystemEvent.Type type, File src, File target) {

        fsListenerSupport.notifyListeners(new FileSystemEvent(type, src, target));

    }

    protected void notifyFileSystemListenersFailed(FileSystemEvent.Type type, FileSystemError error, File src, File target) {

        FileSystemEvent event = new FileSystemEvent(type, src, target);

        event.setError(error);

        fsListenerSupport.notifyListeners(event);
    }
}
