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
package de.elmar_baumann.jpt.image.metadata.exif;

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifType;

/**
 * Checks conditions and throws Exceptions if not fullified.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-28
 */
public final class Ensure {

    /**
     * Ensures that an entry has a specific tag ID.
     *
     * @param  entry entry
     * @param  tag   tag id
     * @throws       IllegalArgumentException if the enty doesn't have that tag ID
     */
    public static void tagId(IfdEntryProxy entry, ExifTag tag) throws IllegalArgumentException {

        if (entry.tagId() != tag.tagId())
            throw new IllegalArgumentException(
                    "Wrong tag: "  + entry.tagId() +
                    ". Expected: " + tag.tagId());
    }

    /**
     * Ensures that an entry is of a specific type.
     *
     * @param  entry entry
     * @param  type  type
     * @throws       IllegalArgumentException if the entry doesn't have that type
     */
    public static void type(IfdEntryProxy entry, ExifType type) throws IllegalArgumentException {

        if (!entry.type().equals(type))
            throw new IllegalArgumentException(
                    "Wrong type: " + entry.type() +
                    ". Expected: " + type);
    }

    /**
     * Ensures that a value is positive (greater or equals to zero).
     *
     * @param  value value
     * @throws       IllegalArgumentException if the value is negative
     */
    public static void positive(long value) throws IllegalArgumentException {
        if (value < 0)
            throw new IllegalArgumentException("Negativ value: " + value);
    }

    /**
     * Ensures that a fraction is positive (greater or equals to zero).
     *
     * @param  numerator   numerator
     * @param  denominator denominator
     * @throws             IllegalArgumentException if the fraction is negative
     */
    public static void positive(long numerator, long denominator) throws IllegalArgumentException {

        boolean negative = numerator < 0 && denominator > 0 || numerator > 0 && denominator < 0;

        if (negative)
            throw new IllegalArgumentException(
                    "Negative fraction: " + numerator + "/" + denominator);
    }

    /**
     * Ensures that a denominator of a fraction is not zero.
     *
     * @param  denominator denominator
     * @throws             IllegalArgumentException if the denominator is equals
     *                     to zero
     */
    public static void noZeroDivision(long denominator) throws IllegalArgumentException {

        if (denominator ==  0)
            throw new IllegalArgumentException("Zero division");
    }

    /**
     * Ensures the length of a byte array.
     *
     * @param  bytes  array
     * @param  length required length
     * @throws        IllegalArgumentException if the array length is not equals
     *                to <code>length</code>
     */
    public static void length(byte[] bytes, int length) throws IllegalArgumentException {

        if (bytes.length != length)
            throw new IllegalArgumentException(
                    "Illegal length: " + bytes.length +
                    ". Required: "     + length);
    }

    private Ensure() {
    }
}
