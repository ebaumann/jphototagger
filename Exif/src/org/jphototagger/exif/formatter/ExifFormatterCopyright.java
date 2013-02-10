package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.tag.ExifCopyright;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#COPYRIGHT}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterCopyright extends ExifFormatter {

    public static final ExifFormatterCopyright INSTANCE = new ExifFormatterCopyright();

    private ExifFormatterCopyright() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.COPYRIGHT);

        return ExifCopyright.convertRawValueToPhotographerCopyright(exifTag.getRawValue());
    }
}
