package de.elmar_baumann.imv.image.metadata.exif;

/**
 * GPS altitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsAltitude {

    public enum Ref {

        OBOVE_SEA_LEVEL, BELOW_SEA_LEVEL
    }
    private Ref ref;
    private ExifRational value;

    public ExifGpsAltitude(Ref ref, ExifRational value) {
        this.ref = ref;
        this.value = value;
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getValue() {
        return value;
    }
}
