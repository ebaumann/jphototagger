package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#METERING_MODE}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterMeteringMode extends ExifFormatter {

    public static final ExifFormatterMeteringMode INSTANCE =
            new ExifFormatterMeteringMode();
    private static final Map<Integer, String> exifKeyOfMeteringMode =
            new HashMap<Integer, String>();


    static {
        exifKeyOfMeteringMode.put(0, "MeteringModeUnknown"); // NOI18N
        exifKeyOfMeteringMode.put(1, "MeteringModeIntegral"); // NOI18N
        exifKeyOfMeteringMode.put(2, "MeteringModeIntegralCenter"); // NOI18N
        exifKeyOfMeteringMode.put(3, "MeteringModeSpot"); // NOI18N
        exifKeyOfMeteringMode.put(4, "MeteringModeMultiSpot"); // NOI18N
        exifKeyOfMeteringMode.put(5, "MeteringModeMatrix"); // NOI18N
        exifKeyOfMeteringMode.put(6, "MeteringModeSelective"); // NOI18N
    }

    private ExifFormatterMeteringMode() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.METERING_MODE.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfMeteringMode.containsKey(value)) {
                return translation.translate(exifKeyOfMeteringMode.get(value));
            }
        }
        return "?";
    }
}
