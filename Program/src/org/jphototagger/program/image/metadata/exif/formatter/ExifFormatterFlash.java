package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.lib.util.ByteUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.domain.exif.ExifIfdType;
import org.jphototagger.domain.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FLASH}.
 *
 * @author Elmar Baumann
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
