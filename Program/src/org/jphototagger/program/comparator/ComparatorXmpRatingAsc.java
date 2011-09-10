package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpRatingAsc extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 2097919906679796456L;
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft = repo.getXmpOfImageFile(fileLeft);
        Xmp xmpRight = repo.getXmpOfImageFile(fileRight);
        Long ratingLeft = xmpLeft.contains(XmpRatingMetaDataValue.INSTANCE)
                ? (Long) xmpLeft.getValue(XmpRatingMetaDataValue.INSTANCE)
                : null;
        Long ratingRight = xmpRight.contains(XmpRatingMetaDataValue.INSTANCE)
                ? (Long) xmpRight.getValue(XmpRatingMetaDataValue.INSTANCE)
                : null;

        return ((ratingLeft == null) && (ratingRight == null))
                ? 0
                : ((ratingLeft == null) && (ratingRight != null))
                ? -1
                : ((ratingLeft != null) && (ratingRight == null))
                ? 1
                : (int) (ratingLeft - ratingRight);
    }
}
