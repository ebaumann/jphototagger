package org.jphototagger.program.image.metadata.exif.formatter.nikon;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.formatter.ExifRawValueFormatter;

import java.nio.ByteOrder;

import java.text.DecimalFormat;

/**
 * Formats tag 18 of the Nikon Type 3 Makernote Tags: The Flash Compensation.
 *
 * @author Elmar Baumann
 */
public final class NikonMakerNoteType3Tag132 implements ExifRawValueFormatter {
    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        byte[] rawValue = exifTag.rawValue();

        if (rawValue.length != 32) {
            return "?";
        }

        byte[] minFocalLengthRawValue = new byte[8];
        byte[] maxFocalLengthRawValue = new byte[8];
        byte[] minFStopRawValue = new byte[8];
        byte[] maxFStopRawValue = new byte[8];

        System.arraycopy(rawValue, 0, minFocalLengthRawValue, 0, 8);
        System.arraycopy(rawValue, 8, maxFocalLengthRawValue, 0, 8);
        System.arraycopy(rawValue, 16, minFStopRawValue, 0, 8);
        System.arraycopy(rawValue, 24, maxFStopRawValue, 0, 8);

        ByteOrder byteOrder = exifTag.byteOrder();
        ExifRational minFocalLengthR = new ExifRational(minFocalLengthRawValue, byteOrder);
        ExifRational maxFocalLengthR = new ExifRational(maxFocalLengthRawValue, byteOrder);
        ExifRational minFStopR = new ExifRational(minFStopRawValue, byteOrder);
        ExifRational maxFStopR = new ExifRational(maxFStopRawValue, byteOrder);
        boolean fixFocalLength = minFocalLengthR.equals(maxFocalLengthR);
        boolean fixFStop = minFStopR.equals(maxFStopR);
        double minFocalLength = ExifDatatypeUtil.toDouble(minFocalLengthR);
        double maxFocalLength = ExifDatatypeUtil.toDouble(maxFocalLengthR);
        double minFStop = ExifDatatypeUtil.toDouble(minFStopR);
        double maxFStop = ExifDatatypeUtil.toDouble(maxFStopR);
        DecimalFormat df = new DecimalFormat("#.#");
        String focalLength = fixFocalLength
                             ? df.format(minFocalLength) + " mm"
                             : df.format(minFocalLength) + "-" + df.format(maxFocalLength) + " mm";
        String fStop = fixFStop
                       ? " 1:" + df.format(minFStop)
                       : " 1:" + df.format(minFStop) + "-" + df.format(maxFStop);

        return focalLength + fStop;
    }
}
