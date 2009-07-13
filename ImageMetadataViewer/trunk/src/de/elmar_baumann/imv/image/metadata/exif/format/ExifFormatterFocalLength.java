package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the type {@link ExifTag#FOCAL_LENGTH}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterFocalLength extends ExifFormatter {

    public static final ExifFormatterFocalLength INSTANCE =
            new ExifFormatterFocalLength();

    private ExifFormatterFocalLength() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.FOCAL_LENGTH.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifRational.isRawValueByteCountOk(entry.getRawValue())) {
            ExifRational er = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.# mm"); // NOI18N
            return df.format(ExifDatatypeUtil.toDouble(er));
        }
        return "?"; // NOI18N
    }
}
