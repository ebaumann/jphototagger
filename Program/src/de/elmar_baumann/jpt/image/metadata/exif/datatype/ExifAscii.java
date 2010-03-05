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
package de.elmar_baumann.jpt.image.metadata.exif.datatype;

/**
 * EXIF data type ASCII as described in the standard: An 8-bit byte containing
 * one 7-bit ASCII code. The final byte is terminated with NULL.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-04-04
 */
public final class ExifAscii {

    private final String value;

    public ExifAscii(byte[] rawValue) {
        value = decode(rawValue);
    }

    /**
     * Decodes a raw value.
     *
     * @param  rawValue raw value
     * @return          decoded value
     */
    public static String decode(byte[] rawValue) {

        String nullTerminatedValue = new String(rawValue);
        int    length              = nullTerminatedValue.length();

        return length > 0 ? nullTerminatedValue.substring(0, length - 1) : "";
    }

    public static ExifDataType dataType() {
        return ExifDataType.ASCII;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ExifAscii other = (ExifAscii) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return value;
    }
}
