package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.resource.Translation;

/**
 * Formats EXIF metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public abstract class ExifFormatter {

    protected static final Translation translation = new Translation(
            "ExifFieldValueTranslations"); // NOI18N

    /**
     * Formats an EXIF entry.
     *
     * @param  entry entry
     * @return string with formatted entry data
     * @throws IllegalArgumentException if the entry has the wrong type
     */
    public abstract String format(IdfEntryProxy entry);
}
