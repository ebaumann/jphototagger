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
 * Compares the suffixes of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-14
 */
public final class ComparatorFilesSuffixes implements Comparator<File> {

    /**
     * Compares the suffixes of two files case insensitive to sort them in
     * ascending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes ASCENDING_IGNORE_CASE =
        new ComparatorFilesSuffixes(CompareOrder.ASCENDING, CompareCase.IGNORE);
    /**
     * Compares the suffixes of two files case sensitive to sort them in
     * ascending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes ASCENDING_CASE_SENSITIVE =
        new ComparatorFilesSuffixes(CompareOrder.ASCENDING, CompareCase.SENSITIVE);
    /**
     * Compares the suffixes of two files case insensitive to sort them in
     * descending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes DESCENDING_IGNORE_CASE =
        new ComparatorFilesSuffixes(CompareOrder.DESCENDING, CompareCase.IGNORE);
    /**
     * Compares the suffixes of two files case sensitive to sort them in
     * descending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes DESCENDING_CASE_SENSITIVE =
        new ComparatorFilesSuffixes(CompareOrder.DESCENDING, CompareCase.SENSITIVE);
    /** Sort order */
    private final CompareOrder compareOrder;
    /** IGNORE case? */
    private final CompareCase compareCase;

    private ComparatorFilesSuffixes(CompareOrder compareOrder, CompareCase compareCase) {
        this.compareOrder = compareOrder;
        this.compareCase = compareCase;
    }

    @Override
    public int compare(File leftFile, File rightFile) {
        String leftSuffix = leftFile.getName();
        String rightSuffix = rightFile.getName();
        int indexLeftPeriod = leftSuffix.lastIndexOf("."); // NOI18N
        int indexRightPeriod = rightSuffix.lastIndexOf("."); // NOI18N

        leftSuffix = indexLeftPeriod >= 0 && indexLeftPeriod < leftSuffix.length() - 1
            ? leftSuffix.substring(indexLeftPeriod + 1) : ""; // NOI18N
        rightSuffix = indexRightPeriod >= 0 && indexRightPeriod < rightSuffix.length() - 1
            ? rightSuffix.substring(indexRightPeriod + 1) : ""; // NOI18N

        boolean suffixesEquals = leftSuffix.isEmpty() || leftSuffix.isEmpty() ||
            compareCase.equals(CompareCase.IGNORE)
            ? leftSuffix.equalsIgnoreCase(rightSuffix)
            : leftSuffix.equals(rightSuffix);

        if (suffixesEquals) {
            leftSuffix = leftFile.getAbsolutePath();
            rightSuffix = rightFile.getAbsolutePath();
        }

        return compareOrder.equals(CompareOrder.ASCENDING)
            ? compareCase.equals(CompareCase.IGNORE)
            ? leftSuffix.compareToIgnoreCase(rightSuffix)
            : leftSuffix.compareTo(rightSuffix)
            : compareCase.equals(CompareCase.IGNORE)
            ? rightSuffix.compareToIgnoreCase(leftSuffix)
            : rightSuffix.compareTo(leftSuffix);
    }
}
