package org.jphototagger.program.filefilter;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class XmpRatingFileFilter implements FileFilter {
    private final int rating;

    /**
     * Creates a new instance.
     *
     * @param rating required rating
     */
    public XmpRatingFileFilter(int rating) {
        this.rating = rating;
    }

    /**
     * Compares the rating in a XMP sidecar file against the rating value of
     * this instance.
     *
     * @param  imageFile image file
     * @return           true if the image file has a sidecar file and the
     *                   rating in the sidecar file is equal to the rating of
     *                   this instance
     */
    @Override
    public boolean accept(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        Xmp xmp = null;

        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
        } catch (IOException ex) {
            Logger.getLogger(XmpRatingFileFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (xmp == null) {
            return false;
        }

        Object o = xmp.getValue(ColumnXmpRating.INSTANCE);

        if (o instanceof Long) {
            if (o != null) {
                return ((Long) o).longValue() == rating;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final XmpRatingFileFilter other = (XmpRatingFileFilter) obj;

        if (this.rating != other.rating) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 89 * hash + this.rating;

        return hash;
    }
}
