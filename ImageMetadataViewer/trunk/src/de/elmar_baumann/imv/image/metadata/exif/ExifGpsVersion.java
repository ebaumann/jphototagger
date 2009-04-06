package de.elmar_baumann.imv.image.metadata.exif;

/**
 * The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is
 * mandatory when GPSInfo tag is present.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifGpsVersion {

    private int first = Integer.MIN_VALUE;
    private int second = Integer.MIN_VALUE;
    private int third = Integer.MIN_VALUE;
    private int fourth = Integer.MIN_VALUE;

    public ExifGpsVersion(int first, int second, int third, int fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public ExifGpsVersion(byte[] rawValue) {
        if (rawValue != null && isRawValueByteCountOk(rawValue)) {
            first = new Byte(rawValue[0]).intValue();
            second = new Byte(rawValue[1]).intValue();
            third = new Byte(rawValue[2]).intValue();
            fourth = new Byte(rawValue[3]).intValue();
        }
    }

    public static int getRawValueByteCount() {
        return 4;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(6);
        sb.append(Integer.toString(first) + ".");
        sb.append(Integer.toString(second) + ".");
        sb.append(Integer.toString(third) + ".");
        sb.append(Integer.toString(fourth));
        return sb.toString();
    }
}
