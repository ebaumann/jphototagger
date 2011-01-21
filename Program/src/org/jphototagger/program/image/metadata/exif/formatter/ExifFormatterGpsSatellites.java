package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_SATELLITES}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsSatellites extends ExifFormatter {
    public static final ExifFormatterGpsSatellites INSTANCE =
        new ExifFormatterGpsSatellites();

    private ExifFormatterGpsSatellites() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_SATELLITES);

        return ExifAscii.decode(exifTag.rawValue());
    }
}
