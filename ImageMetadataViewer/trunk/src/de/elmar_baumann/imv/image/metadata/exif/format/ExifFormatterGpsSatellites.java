package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;

/**
 * Formats an EXIF entry of the type {@link ExifTag#GPS_SATELLITES}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterGpsSatellites extends ExifFormatter {

    public static final ExifFormatterGpsSatellites INSTANCE =
            new ExifFormatterGpsSatellites();

    private ExifFormatterGpsSatellites() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.GPS_SATELLITES.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        return ExifAscii.decode(entry.getRawValue());
    }
}
