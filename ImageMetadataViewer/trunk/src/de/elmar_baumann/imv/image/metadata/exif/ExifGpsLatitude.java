package de.elmar_baumann.imv.image.metadata.exif;

/**
 * GPS latitude.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsLatitude {

    public enum Ref {

        NORTH, SOUTH
    }
    private Ref ref;
    private ExifRational degrees;
    private ExifRational minutes;
    private ExifRational seconds;

    public ExifGpsLatitude(Ref ref, ExifRational degrees, ExifRational minutes, ExifRational seconds) {
        this.ref = ref;
        this.degrees = degrees;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public ExifRational getDegrees() {
        return degrees;
    }

    public ExifRational getMinutes() {
        return minutes;
    }

    public Ref getRef() {
        return ref;
    }

    public ExifRational getSeconds() {
        return seconds;
    }
}
