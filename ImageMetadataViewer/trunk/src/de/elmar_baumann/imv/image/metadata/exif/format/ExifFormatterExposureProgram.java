package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#EXPOSURE_PROGRAM}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterExposureProgram extends ExifFormatter {

    public static final ExifFormatterExposureProgram INSTANCE =
            new ExifFormatterExposureProgram();
    private static final Map<Integer, String> EXIF_KEY_OF_EXPOSURE_PROGRAM =
            new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(0, "ExposureProgramUnkonwn"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(1, "ExposureProgramManual"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(2, "ExposureProgramNormalProgram"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(3, "ExposureProgramAperturePriority"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(4, "ExposureProgramTimePriority"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(5, "ExposureProgramCreativ"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(6, "ExposureProgramAction"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(7, "ExposureProgramPortrait"); // NOI18N
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(8, "ExposureProgramLandscape"); // NOI18N
    }

    private ExifFormatterExposureProgram() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.EXPOSURE_PROGRAM.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_EXPOSURE_PROGRAM.containsKey(value)) {
                return TRANSLATION.translate(EXIF_KEY_OF_EXPOSURE_PROGRAM.get(
                        value));
            }
        }
        return "?"; // NOI18N
    }
}
