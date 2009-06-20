package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;

/**
 * Formats an EXIF entry of the type {@link ExifTag#FILE_SOURCE}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterFileSource extends ExifFormatter {

    public static final ExifFormatterFileSource INSTANCE =
            new ExifFormatterFileSource();

    private ExifFormatterFileSource() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.FILE_SOURCE.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        byte[] rawValue = entry.getRawValue();
        if (rawValue.length >= 1) {
            int value = rawValue[0];
            if (value == 3) {
                return translation.translate("FileSourceDigitalCamera"); // NOI18N
            }
        }
        return "?";
    }
}
