/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.comparator;

import de.elmar_baumann.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the file names of two files descending case sensitive.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Martin Pohlack
 * @version 2009-12-20
 */
public final class ComparatorFilesNamesDescCs
        extends    ClassEquality
        implements Comparator<File>,
                   Serializable
    {
    private static final long serialVersionUID = -2223682373452148530L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareTo(rightFile.getName()) * -1;
    }
}
