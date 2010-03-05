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

package de.elmar_baumann.jpt.image.metadata.exif.tag;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.image.metadata.exif.Ensure;

import java.nio.ByteOrder;

import java.util.Arrays;

/**
 * A coordinate described in degrees (minutes and seconds are the 1/60 and
 * 1/3600 or a degree).
 *
 * @author  Elmar Baumann
 * @version 2009-03-30
 */
public final class ExifDegrees {
    private final ExifRational degrees;
    private final ExifRational minutes;
    private final ExifRational seconds;

    /**
     * Creates a new instance.
     *
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of raw value is not equals
     *         to {@link #byteCount()}
     */
    public ExifDegrees(byte[] rawValue, ByteOrder byteOrder) {
        Ensure.length(rawValue, byteCount());
        degrees = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8),
                                   byteOrder);
        minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8, 16),
                                   byteOrder);
        seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24),
                                   byteOrder);
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int byteCount() {
        return 24;
    }

    public boolean byteCountOk(byte[] rawValue) {
        return rawValue.length == byteCount();
    }

    public ExifRational degrees() {
        return degrees;
    }

    public ExifRational minutes() {
        return minutes;
    }

    public ExifRational seconds() {
        return seconds;
    }
}
