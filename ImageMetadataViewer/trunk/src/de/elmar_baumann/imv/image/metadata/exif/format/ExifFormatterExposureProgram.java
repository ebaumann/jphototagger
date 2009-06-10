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
public final class ExifFormatterExposureProgram extends ExifFormatter {

    public static final ExifFormatterExposureProgram INSTANCE =
            new ExifFormatterExposureProgram();
    private static final Map<Integer, String> exifKeyOfExposureProgram =
            new HashMap<Integer, String>();


    static {
        exifKeyOfExposureProgram.put(0, "ExposureProgramUnkonwn"); // NOI18N
        exifKeyOfExposureProgram.put(1, "ExposureProgramManual"); // NOI18N
        exifKeyOfExposureProgram.put(2, "ExposureProgramNormalProgram"); // NOI18N
        exifKeyOfExposureProgram.put(3, "ExposureProgramAperturePriority"); // NOI18N
        exifKeyOfExposureProgram.put(4, "ExposureProgramTimePriority"); // NOI18N
        exifKeyOfExposureProgram.put(5, "ExposureProgramCreativ"); // NOI18N
        exifKeyOfExposureProgram.put(6, "ExposureProgramAction"); // NOI18N
        exifKeyOfExposureProgram.put(7, "ExposureProgramPortrait"); // NOI18N
        exifKeyOfExposureProgram.put(8, "ExposureProgramLandscape"); // NOI18N
    }

    private ExifFormatterExposureProgram() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.EXPOSURE_PROGRAM.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfExposureProgram.containsKey(value)) {
                return translation.translate(exifKeyOfExposureProgram.get(value));
            }
        }
        return "?";
    }
}
