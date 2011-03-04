package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Compares the last modification time of two files descending.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesLastModifiedDesc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 5961294262855141993L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = fileLeft.lastModified();
        long timeRight = fileRight.lastModified();

        return (timeLeft == timeRight)
               ? 0
               : (timeLeft > timeRight)
                 ? -1
                 : 1;
    }
}
