package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import java.io.File;
import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorXmpIptcLocationDesc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -3947931666327801561L;
    private transient Collator collator = Collator.getInstance();

    @Override
    public int compare(File fileLeft, File fileRight) {
        Xmp xmpLeft = DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileLeft);
        Xmp xmpRight = DatabaseImageFiles.INSTANCE.getXmpOfImageFile(fileRight);
        Object locLeft = (xmpLeft == null)
                         ? null
                         : xmpLeft.getValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        Object locRight = (xmpRight == null)
                          ? null
                          : xmpRight.getValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE);

        return ((locLeft == null) && (locRight == null))
               ? 0
               : ((locLeft == null) && (locRight != null))
                 ? 1
                 : ((locLeft != null) && (locRight == null))
                   ? -1
                   : collator.compare(locRight, locLeft);
    }
}
