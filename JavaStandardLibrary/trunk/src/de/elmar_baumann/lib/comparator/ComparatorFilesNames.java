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

import java.io.File;
import java.util.Comparator;

/**
 * Compares the absolute path names of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-14
 */
public final class ComparatorFilesNames implements Comparator<File> {

    /**
     * Compares the names of two files case insensitive to sort them in
     * ascending order.
     */
    public static final ComparatorFilesNames ASCENDING_IGNORE_CASE =
        new ComparatorFilesNames(CompareOrder.ASCENDING, CompareCase.IGNORE);
    /**
     * Compares the names of two files case sensitive to sort them in
     * ascending order.
     */
    public static final ComparatorFilesNames ASCENDING_CASE_SENSITIVE =
        new ComparatorFilesNames(CompareOrder.ASCENDING, CompareCase.SENSITIVE);
    /**
     * Compares the names of two files case insensitive to sort them in
     * descending order.
     */
    public static final ComparatorFilesNames DESCENDING_IGNORE_CASE =
        new ComparatorFilesNames(CompareOrder.DESCENDING, CompareCase.IGNORE);
    /**
     * Compares the names of two files case sensitive to sort them in
     * descending order.
     */
    public static final ComparatorFilesNames DESCENDING_CASE_SENSITIVE =
        new ComparatorFilesNames(CompareOrder.DESCENDING, CompareCase.SENSITIVE);
    /** Sort order of the files */
    private final CompareOrder compareOrder;
    /** Case sensitive? */
    private final CompareCase compareCase;

    private ComparatorFilesNames(CompareOrder compareOrder, CompareCase compareCase) {
        this.compareOrder = compareOrder;
        this.compareCase = compareCase;
    }

    /**
     * Compares the absolute path names of of two files. Uses {@link CompareOrder}
     * and {@link CompareCase}.
     *
     * @param leftFile   left file
     * @param rightFile  right file
     * @return zero if both file paths are equals, less than zero if the
     *         left file's sort order is before the right file's sort order
     *         and greater than zero if the right file's sort order is before
     *         the left file's sort order
     */
    @Override
    public int compare(File leftFile, File rightFile) {
        String leftFilename =
            compareCase.equals(CompareCase.IGNORE)
            ? leftFile.getAbsolutePath().toLowerCase()
            : leftFile.getAbsolutePath();
        String rightFilename =
            compareCase.equals(CompareCase.IGNORE)
            ? rightFile.getAbsolutePath().toLowerCase()
            : rightFile.getAbsolutePath();
        int resultAscending = leftFilename.compareTo(rightFilename);
        return compareOrder.equals(CompareOrder.ASCENDING)
            ? resultAscending : resultAscending * -1;
    }
}
