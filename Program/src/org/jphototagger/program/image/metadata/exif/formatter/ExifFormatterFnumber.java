package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#F_NUMBER}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFnumber extends ExifFormatter {
    public static final ExifFormatterFnumber INSTANCE = new ExifFormatterFnumber();

    private ExifFormatterFnumber() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.F_NUMBER);

        if (ExifRational.byteCount() == exifTag.rawValue().length) {
            ExifRational fNumer = new ExifRational(exifTag.rawValue(), exifTag.byteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();

            df.applyPattern("#.#");

            return df.format(ExifDatatypeUtil.toDouble(fNumer));
        }

        return "?";
    }
}
