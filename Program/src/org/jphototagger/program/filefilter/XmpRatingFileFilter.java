package org.jphototagger.program.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    public enum Compare {
        LESS,
        LESS_OR_EQUALS,
        EQUALS,
        GREATER_OR_EQUALS,
        GREATER,
    }

    private static final String DISPLAY_NAME_UNDEFINED = Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.DisplayName.Undefined");
    private final Compare compare;
    private final int rating;
    private final Map<Integer, String> displayNameOfRating = new HashMap<>();

    XmpRatingFileFilter(Compare compare, int rating) {
        this.compare = Objects.requireNonNull(compare, "compare == null");
        this.rating = rating;
        init();
    }

    private void init() {
        displayNameOfRating.put(0, getDisplayname(compare, 0));
        displayNameOfRating.put(1, getDisplayname(compare, 1));
        displayNameOfRating.put(2, getDisplayname(compare, 2));
        displayNameOfRating.put(3, getDisplayname(compare, 3));
        displayNameOfRating.put(4, getDisplayname(compare, 4));
        displayNameOfRating.put(5, getDisplayname(compare, 5));
    }

    private String getDisplayname(Compare compare, int rating) {
        switch (compare) {
            case GREATER_OR_EQUALS:
                switch (rating) {
                    case 0: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.0Stars");
                    case 1: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.1Star");
                    case 2: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.2Stars");
                    case 3: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.3Stars");
                    case 4: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.4Stars");
                    case 5: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER_OR_EQUALS.DisplayName.5Stars");
                    default: return compare.name() + " " + rating;
                }
            case EQUALS:
                switch (rating) {
                    case 0: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.0Stars");
                    case 1: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.1Star");
                    case 2: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.2Stars");
                    case 3: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.3Stars");
                    case 4: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.4Stars");
                    case 5: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.EQUALS.DisplayName.5Stars");
                    default: return compare.name() + " " + rating;
                }
            case GREATER:
                switch (rating) {
                    case 0: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.0Stars");
                    case 1: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.1Star");
                    case 2: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.2Stars");
                    case 3: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.3Stars");
                    case 4: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.4Stars");
                    case 5: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.GREATER.DisplayName.5Stars");
                    default: return compare.name() + " " + rating;
                }
            case LESS:
                switch (rating) {
                    case 0: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.0Stars");
                    case 1: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.1Star");
                    case 2: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.2Stars");
                    case 3: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.3Stars");
                    case 4: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.4Stars");
                    case 5: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS.DisplayName.5Stars");
                    default: return compare.name() + " " + rating;
                }
            case LESS_OR_EQUALS:
                switch (rating) {
                    case 0: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.0Stars");
                    case 1: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.1Star");
                    case 2: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.2Stars");
                    case 3: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.3Stars");
                    case 4: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.4Stars");
                    case 5: return Bundle.getString(XmpRatingFileFilter.class, "XmpRatingFileFilter.LESS_OR_EQUALS.DisplayName.5Stars");
                    default: return compare.name() + " " + rating;
                }
            default:
                return compare.name() + " " + rating;
        }
    };

    /**
     * Compares the rating in a XMP sidecar file against the rating value of
     * this instance.
     *
     * @param  imageFile image file
     * @return           true if the image file has a sidecar file and the
     *                   rating in the sidecar file is equal to or greater
     *                   than the rating of this instance
     */
    @Override
    public boolean accept(File imageFile) {
        Objects.requireNonNull(imageFile, "imageFile == null");

        Xmp xmp = null;

        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
        } catch (IOException ex) {
            Logger.getLogger(XmpRatingFileFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        Object fileRating = xmp == null
                ? 0L
                : xmp.getValue(XmpRatingMetaDataValue.INSTANCE);
        long value = fileRating instanceof Long
                ? (Long) fileRating
                : 0;

        switch (compare) {
            case GREATER_OR_EQUALS: return value >= rating;
            case EQUALS: return value == rating;
            case GREATER: return value > rating;
            case LESS: return value < rating;
            case LESS_OR_EQUALS: return value <= rating;
            default:
                Logger.getLogger(XmpRatingFileFilter.class.getName()).log(Level.WARNING, "Not handled comparison: {0}", compare);
                return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof XmpRatingFileFilter)) {
            return false;
        }

        XmpRatingFileFilter other = (XmpRatingFileFilter) obj;

        return this.rating == other.rating && this.compare == other.compare;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.compare);
        hash = 53 * hash + this.rating;
        return hash;
    }

    @Override
    public String getDisplayName() {
        String displayName = displayNameOfRating.get(rating);
        return displayName == null
                ? DISPLAY_NAME_UNDEFINED
                : displayName;
    }
}
