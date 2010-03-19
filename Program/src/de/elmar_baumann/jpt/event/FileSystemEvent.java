/*
 * @(#)FileSystemEvent.java    Created on 2008-10-20
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

package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.io.FileSystemError;

import java.io.File;

/**
 * Event in a file system.
 *
 * @author  Elmar Baumann
 */
public final class FileSystemEvent {
    public enum Type {
        COPY, DELETE, MOVE, RENAME
    }

    private final Type      type;
    private final File      source;
    private final File      target;
    private FileSystemError error;

    public FileSystemEvent(Type type, File source, File target) {
        this.type   = type;
        this.source = source;
        this.target = target;
    }

    public boolean isError() {
        return error != null;
    }

    public FileSystemError getError() {
        return error;
    }

    public void setError(FileSystemError error) {
        this.error = error;
    }

    public Type getType() {
        return type;
    }

    public File getSource() {
        return source;
    }

    public File getTarget() {
        return target;
    }
}
