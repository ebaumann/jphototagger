package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#SHARPNESS}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterSharpness extends ExifFormatter {

    public static final ExifFormatterSharpness INSTANCE =
            new ExifFormatterSharpness();
    private static final Map<Integer, String> EXIF_KEY_OF_SHARPNESS =
            new HashMap<Integer, String>();


    static {
        EXIF_KEY_OF_SHARPNESS.put(0, "SharpnessNormal"); // NOI18N
        EXIF_KEY_OF_SHARPNESS.put(1, "SharpnessSoft"); // NOI18N
        EXIF_KEY_OF_SHARPNESS.put(2, "SharpnessHard"); // NOI18N
    }

    private ExifFormatterSharpness() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.SHARPNESS.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.getRawValueByteCount() == entry.getRawValue().length) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_SHARPNESS.containsKey(value)) {
                return TRANSLATION.translate(EXIF_KEY_OF_SHARPNESS.get(value));
            }
        }
        return "?"; // NOI18N
    }
}
