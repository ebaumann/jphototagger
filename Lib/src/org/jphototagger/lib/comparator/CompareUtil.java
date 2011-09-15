package org.jphototagger.lib.comparator;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
final class CompareUtil {

    static FileSuffixes createFileSuffixes(File leftFile, File rightFile, boolean ignoreCase) {
        String leftSuffix = CompareUtil.getFilenameSuffix(leftFile);
        String rightSuffix = CompareUtil.getFilenameSuffix(rightFile);
        boolean suffixesEquals = ignoreCase
                ? leftSuffix.equalsIgnoreCase(rightSuffix)
                : leftSuffix.equals(rightSuffix);

        if (suffixesEquals) {
            leftSuffix = leftFile.getAbsolutePath();
            rightSuffix = rightFile.getAbsolutePath();
        }

        return new FileSuffixes(leftSuffix, rightSuffix);
    }

    private static String getFilenameSuffix(File file) {
        String suffix = file.getName();
        int indexPeriod = suffix.lastIndexOf('.');

        return ((indexPeriod >= 0) && (indexPeriod < suffix.length() - 1))
                ? suffix.substring(indexPeriod + 1)
                : "";
    }

    private CompareUtil() {
    }
}
