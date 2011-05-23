package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#ISO_SPEED_RATINGS}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterIsoSpeedRatings extends ExifFormatter {
    public static final ExifFormatterIsoSpeedRatings INSTANCE = new ExifFormatterIsoSpeedRatings();
    private static final String POSTFIX = " ISO";

    private ExifFormatterIsoSpeedRatings() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.ISO_SPEED_RATINGS);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());

            return Integer.toString(es.getValue()) + POSTFIX;
        }

        return "?" + POSTFIX;
    }
}
