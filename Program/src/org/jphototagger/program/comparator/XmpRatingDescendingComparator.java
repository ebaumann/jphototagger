package org.jphototagger.program.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.util.ClassEquality;

/**
 *
 * @author Elmar Baumann
 */
public final class XmpRatingDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -6367296007733860352L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft = repo.findXmpOfImageFile(fileLeft);
        Xmp xmpRight = repo.findXmpOfImageFile(fileRight);
        Long ratingLeft = xmpLeft.contains(XmpRatingMetaDataValue.INSTANCE)
                ? (Long) xmpLeft.getValue(XmpRatingMetaDataValue.INSTANCE)
                : null;
        Long ratingRight = xmpRight.contains(XmpRatingMetaDataValue.INSTANCE)
                ? (Long) xmpRight.getValue(XmpRatingMetaDataValue.INSTANCE)
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
