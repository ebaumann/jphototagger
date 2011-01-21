package org.jphototagger.lib.comparator;

import org.jphototagger.lib.generics.Pair;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
final class Util {
    static Pair<String, String> getCmpSuffixes(File leftFile, File rightFile,
            boolean ignoreCase) {
        String  leftSuffix     = Util.filenameSuffix(leftFile);
        String  rightSuffix    = Util.filenameSuffix(rightFile);
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
        int    indexPeriod = suffix.lastIndexOf('.');

        return ((indexPeriod >= 0) && (indexPeriod < suffix.length() - 1))
               ? suffix.substring(indexPeriod + 1)
               : "";
    }

    private Util() {}
}
