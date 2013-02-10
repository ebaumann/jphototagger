package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#ISO_SPEED_RATINGS}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterIsoSpeedRatings extends ExifFormatter {

    public static final ExifFormatterIsoSpeedRatings INSTANCE = new ExifFormatterIsoSpeedRatings();
    private static final String POSTFIX = " ISO";

    private ExifFormatterIsoSpeedRatings() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.ISO_SPEED_RATINGS);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());

            return Integer.toString(es.getValue()) + POSTFIX;
        }

        return "?" + POSTFIX;
    }
}
