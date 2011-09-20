package org.jphototagger.domain.comparator;

import java.io.File;
import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.lib.util.ClassEquality;

/**
 *
 * @author Elmar Baumann
 */
public final class XmpIptcLocationDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -3947931666327801561L;
    private transient Collator collator = Collator.getInstance();
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft = repo.findXmpOfImageFile(fileLeft);
        Xmp xmpRight = repo.findXmpOfImageFile(fileRight);
        Object locLeft = (xmpLeft == null)
                ? null
                : xmpLeft.getValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        Object locRight = (xmpRight == null)
                ? null
                : xmpRight.getValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);

        return ((locLeft == null) && (locRight == null))
                ? 0
                : ((locLeft == null) && (locRight != null))
                ? 1
                : ((locLeft != null) && (locRight == null))
                ? -1
                : collator.compare(locRight, locLeft);
    }
}
