package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.lib.util.ByteUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

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

        byte[] rawValue = exifTag.rawValue();

        if ((rawValue != null) && (rawValue.length >= 1)) {
            boolean[] bitsByte1 = ByteUtil.getBits(rawValue[0]);
            boolean fired = bitsByte1[0];
            boolean hasFlash = !bitsByte1[5];

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
