/*
 * @(#)ExifFormatterGpsVersionId.java    Created on 2009-06-10
 *
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

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifByte;
import de.elmar_baumann.jpt.image.metadata.exif.Ensure;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;

import java.util.Arrays;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_VERSION_ID}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterGpsVersionId extends ExifFormatter {
    public static final ExifFormatterGpsVersionId INSTANCE =
        new ExifFormatterGpsVersionId();

    private ExifFormatterGpsVersionId() {}

    @Override
    public String format(ExifTag exifTag) {
        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_VERSION_ID);

        byte[] rawValue = exifTag.rawValue();

        assert rawValue.length == 4 : rawValue.length;

        if (rawValue.length != 4) {
            return new String(rawValue);
        }

        ExifByte first  = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third  = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.value() + "." + second.value() + "." + third.value() + "."
               + fourth.value();
    }
}
