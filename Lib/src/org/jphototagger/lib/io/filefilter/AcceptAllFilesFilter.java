/*
 * @(#)AcceptAllFilesFilter.java    Created on 2009-05-22
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

import org.jphototagger.lib.resource.JslBundle;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * Accepts all Files, rejects directories.
 *
 * @author  Elmar Baumann
 */
public final class AcceptAllFilesFilter implements FileFilter, Serializable {
    public static final AcceptAllFilesFilter INSTANCE =
        new AcceptAllFilesFilter();
    private static final long serialVersionUID = 6297923800725402735L;

    private AcceptAllFilesFilter() {}

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile()
               ? true
               : false;
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser() {
        return new FileChooserFilter(
            this,
            JslBundle.INSTANCE.getString("AcceptAllFilesFilter.Description"));
    }
}
