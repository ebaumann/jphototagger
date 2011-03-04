package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.lib.util.ByteUtil;

/**
 * The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is
 * mandatory when GPSInfo tag is present.
 *
 * @author Elmar Baumann
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

    /**
     *
     * @param rawValue can be null
     */
    public ExifGpsVersion(byte[] rawValue) {
        if ((rawValue != null) && byteCountOk(rawValue)) {
            first = ByteUtil.toInt(rawValue[0]);
            second = ByteUtil.toInt(rawValue[1]);
            third = ByteUtil.toInt(rawValue[2]);
            fourth = ByteUtil.toInt(rawValue[3]);
        }
    }

    public static int byteCount() {
        return 4;
    }

    public static boolean byteCountOk(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        return rawValue.length == byteCount();
    }

    public int first() {
        return first;
    }

    public int fourth() {
        return fourth;
    }

    public int second() {
        return second;
    }

    public int third() {
        return third;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6);

        sb.append(Integer.toString(first)).append(".");
        sb.append(Integer.toString(second)).append(".");
        sb.append(Integer.toString(third)).append(".");
        sb.append(Integer.toString(fourth));

        return sb.toString();
    }
}
