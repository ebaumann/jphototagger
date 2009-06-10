package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats EXIF metadata fields in ASCII format.
 *
 * @author  Elmar Baumann <ebaumann@feitsch.de>
 * @version 2009/06/10
 */
public final class ExifFormatterAscii extends ExifFormatter {

    private static final List<Integer> asciiTags = new ArrayList<Integer>();
    public static final ExifFormatterAscii INSTANCE = new ExifFormatterAscii();

    private ExifFormatterAscii() {
    }


    static {
        // Ordered alphabetically for faster checks
        // *****************************************************
        // *** Add every new tag ID to ExifFormatterFactory! ***
        // *****************************************************
        asciiTags.add(ExifTag.ARTIST.getId());
        asciiTags.add(ExifTag.IMAGE_DESCRIPTION.getId());
        asciiTags.add(ExifTag.MAKE.getId());
        asciiTags.add(ExifTag.MODEL.getId());
        asciiTags.add(ExifTag.SOFTWARE.getId());
    }

    @Override
    public String format(IdfEntryProxy entry) {
        boolean isAsciiTag = asciiTags.contains(entry.getTag());
        if (!isAsciiTag) throw new IllegalArgumentException(
                    "Not an ASCII-Tag: " + entry);
        return ExifAscii.decode(entry.getRawValue());
    }
}
