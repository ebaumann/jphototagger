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

import de.elmar_baumann.lib.util.ClassNameEquality;
import java.io.File;
import java.util.Comparator;

/**
 * Compares the file names of two files ascending case insensitive.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Martin Pohlack
 * @version 2009-12-20
 */
public final class ComparatorFilesNamesAscCi
        extends    ClassNameEquality
        implements Comparator<File> {

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareToIgnoreCase(rightFile.getName());
    }
}
