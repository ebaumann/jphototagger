package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#SATURATION}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterSaturation extends ExifFormatter {

    public static final ExifFormatterSaturation INSTANCE =
            new ExifFormatterSaturation();
    private static final Map<Integer, String> exifKeyOfSaturation =
            new HashMap<Integer, String>();


    static {
        exifKeyOfSaturation.put(0, "SaturationNormal"); // NOI18N
        exifKeyOfSaturation.put(1, "SaturationLow"); // NOI18N
        exifKeyOfSaturation.put(2, "SaturationHigh"); // NOI18N
    }

    private ExifFormatterSaturation() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.SATURATION.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfSaturation.containsKey(value)) {
                return translation.translate(exifKeyOfSaturation.get(value));
            }
        }
        return "?";
    }
}
