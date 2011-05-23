package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.tag.ExifCopyright;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#COPYRIGHT}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterCopyright extends ExifFormatter {
    public static final ExifFormatterCopyright INSTANCE = new ExifFormatterCopyright();

    private ExifFormatterCopyright() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.COPYRIGHT);

        return ExifCopyright.convertRawValueToPhotographerCopyright(exifTag.getRawValue());
    }
}
