package org.jphototagger.exif.formatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifRational;
import org.jphototagger.exif.datatype.ExifValueUtil;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#F_NUMBER}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFnumber extends ExifFormatter {

    public static final ExifFormatterFnumber INSTANCE = new ExifFormatterFnumber();

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }
        Ensure.exifTagId(exifTag, ExifTag.Properties.F_NUMBER);
        if (ExifRational.getRawValueByteCount() == exifTag.getRawValue().length) {
            ExifRational fNumer = new ExifRational(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            if (ExifValueUtil.convertExifRationalToDouble(fNumer) <= 0) {
                return "?";
            }
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.#");
            return df.format(ExifValueUtil.convertExifRationalToDouble(fNumer));
        }
        return "?";
    }

    private ExifFormatterFnumber() {
    }
}
