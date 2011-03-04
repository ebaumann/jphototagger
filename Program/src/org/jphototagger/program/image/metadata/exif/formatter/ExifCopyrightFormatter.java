package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.tag.ExifCopyright;

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

        return ExifCopyright.photographerCopyright(exifTag.rawValue());
    }

    private ExifCopyrightFormatter() {}
}
