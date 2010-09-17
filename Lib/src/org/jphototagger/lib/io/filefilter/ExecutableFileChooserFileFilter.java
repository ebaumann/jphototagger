/*
 * @(#)ExecutableFileChooserFileFilter.java    Created on 2009-04-01
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
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * BUG: Does not work.
 *
 * @author Elmar Baumann
 */
public final class ExecutableFileChooserFileFilter extends FileFilter
        implements Serializable {
    private static final long serialVersionUID = 8245956226000329520L;

    @Override
    public boolean accept(File f) {
        return f.canExecute();
    }

    @Override
    public String getDescription() {
        return JslBundle.INSTANCE.getString(
            "ExecutableFileChooserFileFilter.Description");
    }
}
