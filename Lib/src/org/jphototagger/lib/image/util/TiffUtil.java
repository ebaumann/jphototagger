package org.jphototagger.lib.image.util;

import java.nio.ByteOrder;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TiffUtil {

    /**
     * Returns a 8 byte TIFF header.
     *
     * @param  byteOrder byte order of this TIFF file
     * @return           TIFF header
     */
    public static byte[] tiffHeader(ByteOrder byteOrder) {
        if (byteOrder == null) {
            throw new NullPointerException("byteOrder == null");
        }

        boolean littleEndian = byteOrder.equals(ByteOrder.LITTLE_ENDIAN);
        byte[] header = new byte[8];
        byte[] bo = littleEndian
                    ? new byte[] { 0x49, 0x49 }
                    : new byte[] { 0x4D, 0x4D };
        byte[] magic = littleEndian
                       ? new byte[] { 42, 0 }
                       : new byte[] { 0, 42 };
        byte[] ifdOffset = littleEndian
                           ? new byte[] { 8, 0, 0, 0 }
                           : new byte[] { 0, 0, 0, 8 };

        System.arraycopy(bo, 0, header, 0, 2);
        System.arraycopy(magic, 0, header, 2, 2);
        System.arraycopy(ifdOffset, 0, header, 4, 4);

        return header;
    }

    private TiffUtil() {}
}
