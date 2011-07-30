package org.jphototagger.exif.formatter;

import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FOCAL_LENGTH_IN_35_MM_FILM}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFocalLengthIn35mm extends ExifFormatter {

    public static final ExifFormatterFocalLengthIn35mm INSTANCE = new ExifFormatterFocalLengthIn35mm();

    private ExifFormatterFocalLengthIn35mm() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM);

        byte[] rawValue = exifTag.getRawValue();
        ByteOrder byteOrder = exifTag.convertByteOrderIdToByteOrder();

        if (ExifShort.isRawValueZeroOrPositive(rawValue, byteOrder)) {
            ExifShort es = new ExifShort(rawValue, byteOrder);
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();

            df.applyPattern("#.# mm");

            return df.format(es.getValue());
        }

        return "?";
    }
}
