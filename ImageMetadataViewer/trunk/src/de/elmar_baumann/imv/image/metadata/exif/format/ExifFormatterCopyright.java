package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.entry.ExifCopyright;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;

/**
 * Formats an EXIF entry of the type {@link ExifTag#COPYRIGHT}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterCopyright extends ExifFormatter {

    public static final ExifFormatterCopyright INSTANCE =
            new ExifFormatterCopyright();

    private ExifFormatterCopyright() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.COPYRIGHT.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        return ExifCopyright.getPhotographerCopyright(entry.getRawValue());
    }
}
