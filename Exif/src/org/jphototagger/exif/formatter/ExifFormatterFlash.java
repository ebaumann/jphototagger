package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifAscii;
import org.jphototagger.lib.util.ByteUtil;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FLASH}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFlash extends ExifFormatter {

    public static final ExifFormatterFlash INSTANCE = new ExifFormatterFlash();

    private ExifFormatterFlash() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FLASH);

        byte[] rawValue = exifTag.getRawValue();

        if ((rawValue != null) && (rawValue.length >= 1)) {
            boolean[] bitsByte1 = ByteUtil.getBits(rawValue[0]);
            boolean fired = bitsByte1[0];
            boolean hasFlash = !bitsByte1[5];

            if (!hasFlash) {
                return translate(ExifIfdType.EXIF, "FlashNone");
            }

            return fired
                    ? translate(ExifIfdType.EXIF, "FlashFired")
                    : translate(ExifIfdType.EXIF, "FlashNotFired");
        }

        return ExifAscii.convertRawValueToString(rawValue);
    }
}
