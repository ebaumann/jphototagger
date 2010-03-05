/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.lib.generics.Pair;
import java.io.File;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-14
 */
final class Util {

    static Pair<String, String> getCmpSuffixes(File leftFile, File rightFile, boolean ignoreCase) {
        String leftSuffix  = Util.filenameSuffix(leftFile);
        String rightSuffix = Util.filenameSuffix(rightFile);

        boolean suffixesEquals = ignoreCase
                ? leftSuffix.equalsIgnoreCase(rightSuffix)
                : leftSuffix.equals(rightSuffix);

        if (suffixesEquals) {
            leftSuffix  = leftFile.getAbsolutePath();
            rightSuffix = rightFile.getAbsolutePath();
        }

        return new Pair<String, String>(leftSuffix, rightSuffix);
    }

    private static String filenameSuffix(File file) {
        String suffix      = file.getName();
        int    indexPeriod = suffix.lastIndexOf(".");

        return indexPeriod >= 0 && indexPeriod < suffix.length() - 1
            ? suffix.substring(indexPeriod + 1)
            : "";
    }

    private Util() {
    }
}
