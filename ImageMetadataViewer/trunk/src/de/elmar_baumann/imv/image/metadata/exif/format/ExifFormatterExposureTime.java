package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.lib.generics.Pair;

/**
 * Formats an EXIF entry of the type {@link ExifTag#EXPOSURE_TIME}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
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
