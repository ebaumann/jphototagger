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

package de.elmar_baumann.lib.image.util;

import java.nio.ByteOrder;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-01-03
 */
public final class TiffUtil {

    /**
     * Returns a 8 byte TIFF header.
     *
     * @param  byteOrder byte order of this TIFF file
     * @return           TIFF header
     */
    public static byte[] tiffHeader(ByteOrder byteOrder) {
        boolean littleEndian = byteOrder.equals(ByteOrder.LITTLE_ENDIAN);
        byte[]  header       = new byte[8];
        byte[]  bo           = littleEndian
                               ? new byte[] { 0x49, 0x49 }
                               : new byte[] { 0x4D, 0x4D };
        byte[]  magic        = littleEndian
                               ? new byte[] { 42, 0 }
                               : new byte[] { 0, 42 };
        byte[]  ifdOffset    = littleEndian
                               ? new byte[] { 8, 0, 0, 0 }
                               : new byte[] { 0, 0, 0, 8 };

        System.arraycopy(bo, 0, header, 0, 2);
        System.arraycopy(magic, 0, header, 2, 2);
        System.arraycopy(ifdOffset, 0, header, 4, 4);

        return header;
    }

    private TiffUtil() {}
}
