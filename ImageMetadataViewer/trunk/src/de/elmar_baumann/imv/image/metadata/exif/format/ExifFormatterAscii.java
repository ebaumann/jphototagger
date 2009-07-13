package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats EXIF metadata fields in ASCII format.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterAscii extends ExifFormatter {

    private static final List<Integer> ASCII_TAGS = new ArrayList<Integer>();
    public static final ExifFormatterAscii INSTANCE = new ExifFormatterAscii();

    private ExifFormatterAscii() {
    }

    static {
        // Ordered alphabetically for faster checks
        // *****************************************************
        // *** Add every new tag ID to ExifFormatterFactory! ***
        // *****************************************************
        ASCII_TAGS.add(ExifTag.ARTIST.getId());
        ASCII_TAGS.add(ExifTag.IMAGE_DESCRIPTION.getId());
        ASCII_TAGS.add(ExifTag.IMAGE_UNIQUE_ID.getId());
        ASCII_TAGS.add(ExifTag.MAKE.getId());
        ASCII_TAGS.add(ExifTag.MODEL.getId());
        ASCII_TAGS.add(ExifTag.SOFTWARE.getId());
        ASCII_TAGS.add(ExifTag.SPECTRAL_SENSITIVITY.getId());
    }

    @Override
    public String format(IdfEntryProxy entry) {
        boolean isAsciiTag = ASCII_TAGS.contains(entry.getTag());
        if (!isAsciiTag) throw new IllegalArgumentException(
                    "Not an ASCII-Tag: " + entry); // NOI18N
        return ExifAscii.decode(entry.getRawValue());
    }
}
