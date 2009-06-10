package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.ExifUserComment;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;

/**
 * Formats an EXIF entry of the type {@link ExifTag# }.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterUserComment extends ExifFormatter {

    public static final ExifFormatterUserComment INSTANCE =
            new ExifFormatterUserComment();

    private ExifFormatterUserComment() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.USER_COMMENT.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        return ExifUserComment.decode(entry.getRawValue());
    }
}
