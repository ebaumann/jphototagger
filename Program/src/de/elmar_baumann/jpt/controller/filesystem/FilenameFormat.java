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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.event.listener.FilenameFormatListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Format of a filename.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public abstract class FilenameFormat {

    private File file;
    private File prevFile;
    private String format;
    private final Set<FilenameFormatListener> listeners =
            new HashSet<FilenameFormatListener>();

    /**
     * Returns a format string.
     * 
     * @return string or null if not set
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets a format string.
     * 
     * @param format  format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the affected file.
     * 
     * @param file  file
     */
    public void setFile(File file) {
        this.prevFile = this.file;
        this.file = file;
    }

    /**
     * Returns the affected file.
     * 
     * @return file or null if not set
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the previous set file.
     *
     * @return previous file
     */
    public File getPrevFile() {
        return prevFile;
    }

    /**
     * Tells that the next file will be renamed. This implementation does
     * nothing.
     */
    public void next() {
    }

    /**
     * Returns wheter the format will change.
     * 
     * @return true if changes are possible. This implementation returns true
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * Returns the formatted filename or part of a filename.
     * 
     * @return filename (-part)
     */
    abstract public String format();

    /**
     * Adds a filename format listener.
     *
     * @param listener listener
     */
    public void addFilenameFormatListener(FilenameFormatListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a filename format listener.
     *
     * @param listener listener
     */
    public void removeFilenameFormatListener(FilenameFormatListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Sends a request to all {@link FilenameFormatListener}s.
     *
     * @param request request
     */
    protected void requestListeners(FilenameFormatListener.Request request) {
        synchronized (listeners) {
            for (FilenameFormatListener listener : listeners) {
                listener.request(request);
            }
        }
    }
}
