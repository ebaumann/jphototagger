package de.elmar_baumann.imv.image.metadata.exif;

/**
 * GPS longitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsLongitude {

    public enum Ref {

        EAST, WEST
    }
    private Ref ref;
    private ExifDegrees degrees;

    public ExifGpsLongitude(Ref ref, byte[] degreesRawValue, ExifRational.ByteOrder byteOrder) {
        this.ref = ref;
        this.degrees = new ExifDegrees(degreesRawValue, byteOrder);
    }

    public ExifDegrees getDegrees() {
        return degrees;
    }

    public Ref getRef() {
        return ref;
    }
}
