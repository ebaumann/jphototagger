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
package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.lib.generics.Pair;

/**
 * Formats an EXIF entry of the type {@link ExifTag#EXPOSURE_TIME}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterExposureTime extends ExifFormatter {

    public static final ExifFormatterExposureTime INSTANCE =
            new ExifFormatterExposureTime();

    private ExifFormatterExposureTime() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.EXPOSURE_TIME.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifRational.getRawValueByteCount() == entry.getRawValue().length) {
            ExifRational time = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            Pair<Integer, Integer> pair = getAsExposureTime(time);
            int numerator = pair.getFirst();
            int denominator = pair.getSecond();
            if (denominator > 1) {
                return Integer.toString(numerator) + " / " + // NOI18N
                        Integer.toString(denominator) + " s"; // NOI18N
            } else if (numerator > 1) {
                return Integer.toString(numerator) + " s"; // NOI18N
            }
        }
        return "?"; // NOI18N
    }

    private static Pair<Integer, Integer> getAsExposureTime(ExifRational er) {
        int numerator = er.getNumerator();
        int denominator = er.getDenominator();
        double result = (double) numerator / (double) denominator;
        if (result < 1) {
            return new Pair<Integer, Integer>(1, (int) ((double) denominator /
                    (double) numerator + 0.5));
        } else if (result >= 1) {
            return new Pair<Integer, Integer>((int) ((double) numerator /
                    (double) denominator + 0.5), 1);
        } else {
            return new Pair<Integer, Integer>(0, 0);
        }
    }
}
