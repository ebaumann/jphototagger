package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag# }.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterContrast extends ExifFormatter {

    public static final ExifFormatterContrast INSTANCE =
            new ExifFormatterContrast();
    private static final Map<Integer, String> exifKeyOfContrast =
            new HashMap<Integer, String>();


    static {
        exifKeyOfContrast.put(0, "ContrastNormal"); // NOI18N
        exifKeyOfContrast.put(1, "ContrastLow"); // NOI18N
        exifKeyOfContrast.put(2, "ContrastHigh"); // NOI18N
    }

    private ExifFormatterContrast() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.CONTRAST.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfContrast.containsKey(value)) {
                return translation.translate(exifKeyOfContrast.get(value));
            }
        }
        return "?";
    }
}
