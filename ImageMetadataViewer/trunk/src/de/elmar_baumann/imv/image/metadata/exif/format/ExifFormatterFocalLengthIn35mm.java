package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the type {@link ExifTag#FOCAL_LENGTH_IN_35_MM_FILM}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterFocalLengthIn35mm extends ExifFormatter {

    public static final ExifFormatterFocalLengthIn35mm INSTANCE =
            new ExifFormatterFocalLengthIn35mm();

    private ExifFormatterFocalLengthIn35mm() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.# mm");
            return df.format(es.getValue());
        }
        return "?";
    }
}
