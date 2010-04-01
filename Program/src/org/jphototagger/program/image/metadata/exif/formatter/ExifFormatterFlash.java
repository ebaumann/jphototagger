/*
 * @(#)ExifFormatterFlash.java    Created on 2009-06-10
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

package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.lib.util.ByteUtil;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FLASH}.
 *
 * @author  Elmar Baumann
 */
public final class ExifFormatterFlash extends ExifFormatter {
    public static final ExifFormatterFlash INSTANCE = new ExifFormatterFlash();

    private ExifFormatterFlash() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FLASH);

        byte[] rawValue = exifTag.rawValue();

        if ((rawValue != null) && (rawValue.length >= 1)) {
            boolean[] bitsByte1 = ByteUtil.getBits(rawValue[0]);
            boolean   fired     = bitsByte1[0];
            boolean   hasFlash  = !bitsByte1[5];

            if (!hasFlash) {
                return translate(IfdType.EXIF, "FlashNone");
            }

            return fired
                   ? translate(IfdType.EXIF, "FlashFired")
                   : translate(IfdType.EXIF, "FlashNotFired");
        }

        return ExifAscii.decode(rawValue);
    }
}
