/*
 * @(#)FileChooserFilter.java    Created on 2009-05-30
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

package org.jphototagger.lib.io.filefilter;

import java.io.File;

/**
 * Filter for {@link javax.swing.JFileChooser} created from a
 * {@link java.io.FileFilter}.
 *
 * @author  Elmar Baumann
 */
public final class FileChooserFilter
        extends javax.swing.filechooser.FileFilter {
    private final java.io.FileFilter fileFilter;
    private final String             description;

    public FileChooserFilter(java.io.FileFilter fileFilter,
                             String description) {
        this.fileFilter  = fileFilter;
        this.description = description;
    }

    /**
     * Returns {@link java.io.FileFilter#accept(java.io.File)} from this
     * instance.
     *
     * @param  f  file
     * @return true when accepted
     */
    @Override
    public boolean accept(File f) {
        return fileFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return description;
    }
}
