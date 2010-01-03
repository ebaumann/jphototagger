/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif.formatter.nikon;

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.jpt.image.metadata.exif.formatter.ExifRawValueFormatter;
import java.nio.ByteOrder;
import java.text.DecimalFormat;

/**
 * Formats tag 18 of the Nikon Type 3 Makernote Tags: The Flash Compensation.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-01
 */
public final class NikonMakerNoteType3Tag132 implements ExifRawValueFormatter {

    @Override
    public String format(ExifTag exifTag) {

        byte[] rawValue = exifTag.rawValue();

        if (rawValue.length != 32) return "?";

        byte[] minFocalLengthRawValue = new byte[8];
        byte[] maxFocalLengthRawValue = new byte[8];
        byte[] minFStopRawValue       = new byte[8];
        byte[] maxFStopRawValue       = new byte[8];

        System.arraycopy(rawValue,  0, minFocalLengthRawValue, 0, 8);
        System.arraycopy(rawValue,  8, maxFocalLengthRawValue, 0, 8);
        System.arraycopy(rawValue, 16, minFStopRawValue, 0, 8);
        System.arraycopy(rawValue, 24, maxFStopRawValue, 0, 8);

        ByteOrder    byteOrder       = exifTag.byteOrder();
        ExifRational minFocalLengthR = new ExifRational(minFocalLengthRawValue, byteOrder);
        ExifRational maxFocalLengthR = new ExifRational(maxFocalLengthRawValue, byteOrder);
        ExifRational minFStopR       = new ExifRational(minFStopRawValue, byteOrder);
        ExifRational maxFStopR       = new ExifRational(maxFStopRawValue, byteOrder);

        boolean       fixFocalLength = minFocalLengthR.equals(maxFocalLengthR);
        boolean       fixFStop       = minFStopR.equals(maxFStopR);
        double        minFocalLength = ExifDatatypeUtil.toDouble(minFocalLengthR);
        double        maxFocalLength = ExifDatatypeUtil.toDouble(maxFocalLengthR);
        double        minFStop       = ExifDatatypeUtil.toDouble(minFStopR);
        double        maxFStop       = ExifDatatypeUtil.toDouble(maxFStopR);
        DecimalFormat df             = new DecimalFormat("#.#");

        String focalLength = fixFocalLength
                ? df.format(minFocalLength) + " mm"
                : df.format(minFocalLength) + "-" + df.format(maxFocalLength) + " mm"
                ;
        String fStop = fixFStop
                ? " 1:" + df.format(minFStop)
                : " 1:" + df.format(minFStop) + "-" + df.format(maxFStop);

        return focalLength + fStop;
    }

}
