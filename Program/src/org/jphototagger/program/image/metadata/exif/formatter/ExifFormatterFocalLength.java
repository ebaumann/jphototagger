package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.domain.exif.ExifTag;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FOCAL_LENGTH}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFocalLength extends ExifFormatter {
    public static final ExifFormatterFocalLength INSTANCE = new ExifFormatterFocalLength();

    private ExifFormatterFocalLength() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FOCAL_LENGTH);

        byte[] rawValue = exifTag.getRawValue();

        if (ExifRational.isValid(rawValue, exifTag.convertByteOrderIdToByteOrder())) {
            ExifRational er = new ExifRational(rawValue, exifTag.convertByteOrderIdToByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();

            df.applyPattern("#.# mm");

            return df.format(ExifDatatypeUtil.convertExifRationalToDouble(er));
        }

        return "?";
    }
}
