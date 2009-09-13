/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.image.metadata.exif.entry;

/**
 * The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is
 * mandatory when GPSInfo tag is present.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
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
        sb.append(Integer.toString(first) + "."); // NOI18N
        sb.append(Integer.toString(second) + "."); // NOI18N
        sb.append(Integer.toString(third) + "."); // NOI18N
        sb.append(Integer.toString(fourth));
        return sb.toString();
    }
}
