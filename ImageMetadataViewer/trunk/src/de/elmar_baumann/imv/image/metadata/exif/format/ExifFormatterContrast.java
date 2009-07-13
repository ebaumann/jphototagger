package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#CONTRAST}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterContrast extends ExifFormatter {

    public static final ExifFormatterContrast INSTANCE =
            new ExifFormatterContrast();
    private static final Map<Integer, String> EXIF_KEY_OF_CONTRAST =
            new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_CONTRAST.put(0, "ContrastNormal"); // NOI18N
        EXIF_KEY_OF_CONTRAST.put(1, "ContrastLow"); // NOI18N
        EXIF_KEY_OF_CONTRAST.put(2, "ContrastHigh"); // NOI18N
    }

    private ExifFormatterContrast() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.CONTRAST.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_CONTRAST.containsKey(value)) {
                return TRANSLATION.translate(EXIF_KEY_OF_CONTRAST.get(value));
            }
        }
        return "?";
    }
}
