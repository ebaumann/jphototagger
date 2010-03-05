/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif.tag;

import de.elmar_baumann.lib.util.ByteUtil;

/**
 * The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is
 * mandatory when GPSInfo tag is present.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-17
 */
public final class ExifGpsVersion {

    private int first  = Integer.MIN_VALUE;
    private int second = Integer.MIN_VALUE;
    private int third  = Integer.MIN_VALUE;
    private int fourth = Integer.MIN_VALUE;

    public ExifGpsVersion(int first, int second, int third, int fourth) {
        this.first  = first;
        this.second = second;
        this.third  = third;
        this.fourth = fourth;
    }

    public ExifGpsVersion(byte[] rawValue) {

        if (rawValue != null && byteCountOk(rawValue)) {
            first  = ByteUtil.toInt(rawValue[0]);
            second = ByteUtil.toInt(rawValue[1]);
            third  = ByteUtil.toInt(rawValue[2]);
            fourth = ByteUtil.toInt(rawValue[3]);
        }
    }

    public static int byteCount() {
        return 4;
    }

    public static boolean byteCountOk(byte[] rawValue) {
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

        StringBuffer sb = new StringBuffer(6);
        sb.append(Integer.toString(first)  + ".");
        sb.append(Integer.toString(second) + ".");
        sb.append(Integer.toString(third)  + ".");
        sb.append(Integer.toString(fourth));

        return sb.toString();
    }
}
