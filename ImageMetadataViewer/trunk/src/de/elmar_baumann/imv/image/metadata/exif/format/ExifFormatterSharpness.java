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
    private static final Map<Integer, String> exifKeyOfSharpness =
            new HashMap<Integer, String>();


    static {
        exifKeyOfSharpness.put(0, "SharpnessNormal"); // NOI18N
        exifKeyOfSharpness.put(1, "SharpnessSoft"); // NOI18N
        exifKeyOfSharpness.put(2, "SharpnessHard"); // NOI18N
    }

    private ExifFormatterSharpness() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.SHARPNESS.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.getRawValueByteCount() == entry.getRawValue().length) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfSharpness.containsKey(value)) {
                return translation.translate(exifKeyOfSharpness.get(value));
            }
        }
        return "?";
    }
}
