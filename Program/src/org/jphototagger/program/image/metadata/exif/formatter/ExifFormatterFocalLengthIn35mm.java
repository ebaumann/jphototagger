package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.nio.ByteOrder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FOCAL_LENGTH_IN_35_MM_FILM}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFocalLengthIn35mm extends ExifFormatter {
    public static final ExifFormatterFocalLengthIn35mm INSTANCE = new ExifFormatterFocalLengthIn35mm();

    private ExifFormatterFocalLengthIn35mm() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM);

        byte[] rawValue = exifTag.rawValue();
        ByteOrder byteOrder = exifTag.byteOrder();

        if (ExifShort.isZeroOrPositive(rawValue, byteOrder)) {
            ExifShort es = new ExifShort(rawValue, byteOrder);
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();

            df.applyPattern("#.# mm");

            return df.format(es.value());
        }

        return "?";
    }
}
