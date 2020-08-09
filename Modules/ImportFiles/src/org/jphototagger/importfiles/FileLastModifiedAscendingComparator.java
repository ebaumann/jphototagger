package org.jphototagger.importfiles;

import java.io.File;
import java.util.Comparator;

/**
 * @author Elmar Baumann
 */
public final class FileLastModifiedAscendingComparator implements Comparator<File> {

    public static final FileLastModifiedAscendingComparator INSTANCE = new FileLastModifiedAscendingComparator();

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = fileLeft.lastModified();
        long timeRight = fileRight.lastModified();

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? -1
                : 1;
    }
}
