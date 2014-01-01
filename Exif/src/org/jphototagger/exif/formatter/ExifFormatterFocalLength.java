package org.jphototagger.exif.formatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifRational;
import org.jphototagger.exif.datatype.ExifValueUtil;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#FOCAL_LENGTH}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFocalLength extends ExifFormatter {

    public static final ExifFormatterFocalLength INSTANCE = new ExifFormatterFocalLength();

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }
        Ensure.exifTagId(exifTag, ExifTag.Properties.FOCAL_LENGTH);
        byte[] rawValue = exifTag.getRawValue();
        if (ExifRational.isValid(rawValue, exifTag.convertByteOrderIdToByteOrder())) {
            ExifRational er = new ExifRational(rawValue, exifTag.convertByteOrderIdToByteOrder());
            if (ExifValueUtil.convertExifRationalToDouble(er) <= 0) {
                return "?";
            }
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.# mm");
            return df.format(ExifValueUtil.convertExifRationalToDouble(er));
        }
        return "?";
    }

    private ExifFormatterFocalLength() {
    }
}
