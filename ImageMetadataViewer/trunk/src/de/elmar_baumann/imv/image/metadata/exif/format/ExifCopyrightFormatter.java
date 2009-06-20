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
public final class ExifCopyrightFormatter extends ExifFormatter {

    public static final ExifCopyrightFormatter INSTANCE =
            new ExifCopyrightFormatter();

    private ExifCopyrightFormatter() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.COPYRIGHT.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        return ExifCopyright.getPhotographerCopyright(entry.getRawValue());
    }
}
