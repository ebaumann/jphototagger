/*
 * @(#)FilenameFormat.java    Created on 2008-10-13
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.filesystem;

import java.io.File;

/**
 * Format of a filename.
 *
 * @author Elmar Baumann
 */
public abstract class FilenameFormat {
    private File   file;
    private File   prevFile;
    private String format;

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
        if (format == null) {
            throw new NullPointerException("format == null");
        }

        this.format = format;
    }

    /**
     * Sets the affected file.
     *
     * @param file  file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.prevFile = this.file;
        this.file     = file;
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

        // ignore
    }

    /**
     * Returns the formatted filename or part of a filename.
     *
     * @return filename (-part)
     */
    abstract public String format();
}
