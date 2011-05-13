package org.jphototagger.program.data;

import org.jphototagger.program.resource.JptBundle;
import java.awt.Color;

/**
 *
 * @author Elmar Baumann
 */
public final class ThumbnailFlag {
    private final Color color;
    private final String string;
    public static final ThumbnailFlag ERROR_FILE_NOT_FOUND =
        new ThumbnailFlag(Color.RED, JptBundle.INSTANCE.getString("ThumbnailFlag.Error.FileNotFound"));

    public ThumbnailFlag(Color color, String string) {
        if (color == null) {
            throw new NullPointerException("color == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

        this.color = color;
        this.string = string;
    }

    public Color getColor() {
        return color;
    }

    /**
     *
     * @return Meaning of the flag
     */
    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ThumbnailFlag other = (ThumbnailFlag) obj;

        if ((this.color != other.color) && ((this.color == null) ||!this.color.equals(other.color))) {
            return false;
        }

        if ((this.string == null) ||!this.string.equals(other.string)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 71 * hash + ((this.color != null)
                            ? this.color.hashCode()
                            : 0);
        hash = 71 * hash + ((this.string != null)
                            ? this.string.hashCode()
                            : 0);

        return hash;
    }

    @Override
    public String toString() {
        return string + "=" + color;
    }
}
