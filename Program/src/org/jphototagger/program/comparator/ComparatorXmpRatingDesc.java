package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpRatingDesc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -6367296007733860352L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp  xmpLeft     =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileLeft);
        Xmp  xmpRight    =
            DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileRight);
        Long ratingLeft  = xmpLeft.contains(ColumnXmpRating.INSTANCE)
                           ? (Long) xmpLeft.getValue(ColumnXmpRating.INSTANCE)
                           : null;
        Long ratingRight = xmpRight.contains(ColumnXmpRating.INSTANCE)
                           ? (Long) xmpRight.getValue(ColumnXmpRating.INSTANCE)
                           : null;

        return ((ratingLeft == null) && (ratingRight == null))
               ? 0
               : ((ratingLeft == null) && (ratingRight != null))
                 ? 1
                 : ((ratingLeft != null) && (ratingRight == null))
                   ? -1
                   : (int) (ratingRight - ratingLeft);
    }
}
