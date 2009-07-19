package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.resource.Bundle;
import java.awt.Color;

/**
 * Flag für ein Thumbnail.
 * 
 * @author  Elmar Baumann <eb@elmar-aumann.de>
 * @version 2008-09-09
 */
public final class ThumbnailFlag {

    private final Color color;
    private final String string;

    /**
     * Flag: Datei wurde nicht gefunden.
     */
    public static final ThumbnailFlag ERROR_FILE_NOT_FOUND =
        new ThumbnailFlag(Color.RED,
        Bundle.getString("ThumbnailFlag.Error.FileNotFound")); // NOI18N

    /**
     * Konstruktor.
     * 
     * @param color  Farbe des Flags
     * @param string String mit Bedeutung des Flags
     */
    public ThumbnailFlag(Color color, String string) {
        this.color = color;
        this.string = string;
    }

    /**
     * Liefert die Farbe des Flags.
     * 
     * @return Farbe
     */
    public Color getColor() {
        return color;
    }

    /**
     * Liefert den String mit der Bedeutung des Flags.
     * 
     * @return String mit Bedeutung
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
        if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
            return false;
        }
        if (this.string == null || !this.string.equals(other.string)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.color != null ? this.color.hashCode() : 0);
        hash = 71 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
