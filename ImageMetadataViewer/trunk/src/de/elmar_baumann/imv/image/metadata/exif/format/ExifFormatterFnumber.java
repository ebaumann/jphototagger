package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats an EXIF entry of the type {@link ExifTag#F_NUMBER}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterFnumber extends ExifFormatter {

    public static final ExifFormatterFnumber INSTANCE =
            new ExifFormatterFnumber();

    private ExifFormatterFnumber() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.F_NUMBER.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifRational.getRawValueByteCount() == entry.getRawValue().length) {
            ExifRational fNumer = new ExifRational(entry.getRawValue(), entry.
                    getByteOrder());
            DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
            df.applyPattern("#.#"); // NOI18N
            return df.format(ExifDatatypeUtil.toDouble(fNumer));
        }
        return "?";
    }
}
