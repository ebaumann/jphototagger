package org.jphototagger.domain.metadata.exif;

import org.jphototagger.lib.util.ObjectUtil;

/**
 * @author Elmar Baumann
 */
public final class ExifTag {

    /**
     * If {@link #getDisplayName()} returns this, {@link #getDisplayValue()} returns
     * a Google Maps URL.
     */
    public static final String NAME_GOOGLE_MAPS_URL = "Google Maps URL";
    private final String displayName;
    private final String displayValue;

    /**
     *
     * @param displayName localized name, e.g. "Focal Length"
     * @param displayValue localized value, e.g. "24.5 mm"
     */
    public ExifTag(String displayName, String displayValue) {
        if (displayName == null) {
            throw new NullPointerException("displayName == null");
        }

        if (displayValue == null) {
            throw new NullPointerException("displayValue == null");
        }

        this.displayName = displayName;
        this.displayValue = displayValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ExifTag)) {
            return false;
        }

        ExifTag other = (ExifTag) obj;

        return ObjectUtil.equals(displayName, other.displayName);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return displayName + ": " + displayValue;
    }
}
