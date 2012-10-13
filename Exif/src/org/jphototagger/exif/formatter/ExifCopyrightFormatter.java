package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.tag.ExifCopyright;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#COPYRIGHT}.
 *
 * @author Elmar Baumann
 */
public final class ExifCopyrightFormatter extends ExifFormatter {

    public static final ExifCopyrightFormatter INSTANCE = new ExifCopyrightFormatter();

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.COPYRIGHT);

        return ExifCopyright.convertRawValueToPhotographerCopyright(exifTag.getRawValue());
    }

    private ExifCopyrightFormatter() {
    }
}
