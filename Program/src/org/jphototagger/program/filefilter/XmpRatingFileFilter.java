package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.xmp.XmpMetadata;

/**
 * @author Elmar Baumann
 */
public final class XmpRatingFileFilter implements FileFilter, DisplayNameProvider {

    private final int rating;
    private static final String DISPLAY_NAME_UNDEFINED = Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.Undefined");
    private static final Map<Integer, String> DISPLAY_NAME_OF_RATING = new HashMap<>();

    static {
        DISPLAY_NAME_OF_RATING.put(1, Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.1Star"));
        DISPLAY_NAME_OF_RATING.put(2, Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.2Stars"));
        DISPLAY_NAME_OF_RATING.put(3, Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.3Stars"));
        DISPLAY_NAME_OF_RATING.put(4, Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.4Stars"));
        DISPLAY_NAME_OF_RATING.put(5, Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.5Stars"));
    }

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

        Object o = xmp.getValue(XmpRatingMetaDataValue.INSTANCE);

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

    @Override
    public String getDisplayName() {
        String displayName = DISPLAY_NAME_OF_RATING.get(rating);

        return displayName == null
                ? DISPLAY_NAME_UNDEFINED
                : displayName;
    }
}
