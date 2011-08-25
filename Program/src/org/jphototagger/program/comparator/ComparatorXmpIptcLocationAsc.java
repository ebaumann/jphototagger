package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import java.io.File;
import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpIptcLocationAsc extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -6946394073635783198L;
    private transient Collator collator = Collator.getInstance();
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft = repo.getXmpOfImageFile(fileLeft);
        Xmp xmpRight = repo.getXmpOfImageFile(fileRight);
        Object locLeft = (xmpLeft == null)
                ? null
                : xmpLeft.getValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        Object locRight = (xmpRight == null)
                ? null
                : xmpRight.getValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE);

        return ((locLeft == null) && (locRight == null))
                ? 0
                : ((locLeft == null) && (locRight != null))
                ? -1
                : ((locLeft != null) && (locRight == null))
                ? 1
                : collator.compare(locLeft, locRight);
    }
}
