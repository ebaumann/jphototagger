/*
 * @(#)ComparatorFilesNoSort.java    Created on 2010-01-19
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

package de.elmar_baumann.lib.comparator;

import de.elmar_baumann.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Does not change the sort order.
 *
 * @author  Elmar Baumann
 */
public final class ComparatorFilesNoSort extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 6632501783143748216L;

    /**
     * Returns always zero.
     *
     * @param  leftFile  1. file to compare
     * @param  rightFile 2. file to compare
     * @return           zero
     */
    @Override
    public int compare(File leftFile, File rightFile) {
        return 0;
    }
}
