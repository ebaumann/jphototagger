/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.image.metadata.exif.formatter;

import de.elmar_baumann.jpt.image.metadata.exif.Ensure;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_TIME_STAMP}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterGpsTimeStamp extends ExifFormatter {

    public static final ExifFormatterGpsTimeStamp INSTANCE = new ExifFormatterGpsTimeStamp();

    private ExifFormatterGpsTimeStamp() {
    }

    @Override
    public String format(ExifTag exifTag) {

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_TIME_STAMP);

        ByteOrder byteOrder = exifTag.byteOrder();
        byte[]    rawValue  = exifTag.rawValue();

        if (rawValue.length != 24) return new String(rawValue);

        ExifRational hours   = new ExifRational(Arrays.copyOfRange(rawValue,  0,  8), byteOrder);
        ExifRational minutes = new ExifRational(Arrays.copyOfRange(rawValue,  8, 16), byteOrder);
        ExifRational seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24), byteOrder);

        int h = (int) ExifDatatypeUtil.toLong(hours);
        int m = (int) ExifDatatypeUtil.toLong(minutes);
        int s = (int) ExifDatatypeUtil.toLong(seconds);

        Calendar cal = Calendar.getInstance();

        cal.set(2009, 4, 3, h, m, s);

        DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);

        return df.format(cal.getTime());
    }
}
