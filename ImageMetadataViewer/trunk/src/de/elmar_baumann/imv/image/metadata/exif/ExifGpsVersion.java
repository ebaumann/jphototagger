package de.elmar_baumann.imv.image.metadata.exif;

/**
 * The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is
 * mandatory when GPSInfo tag is present.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsVersion {

    private int first;
    private int second;
    private int third;
    private int fourth;

    public ExifGpsVersion(int first, int second, int third, int fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public int getFirst() {
        return first;
    }

    public int getFourth() {
        return fourth;
    }

    public int getSecond() {
        return second;
    }

    public int getThird() {
        return third;
    }
}
